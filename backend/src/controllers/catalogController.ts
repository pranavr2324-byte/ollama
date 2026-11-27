import { Request, Response } from 'express';
import { prisma } from '../config/prisma';
import { cloudinaryService } from '../services/cloudinaryService';
import { AppError } from '../utils/AppError';
import { paginationQuerySchema, searchQuerySchema } from '../validators/catalogValidator';
import { formatSongResponse } from './adminController';

async function getUserFavouriteIds(userId?: string): Promise<Set<string>> {
  if (!userId) return new Set();
  const favs = await prisma.favourite.findMany({
    where: { userId },
    select: { songId: true },
  });
  return new Set(favs.map((f) => f.songId));
}

function attachStreamAndFav(
  song: Parameters<typeof formatSongResponse>[0],
  favouriteIds: Set<string>
) {
  const base = formatSongResponse(song);
  let streamUrl: string | null = null;
  try {
    streamUrl = cloudinaryService.getAudioDeliveryUrl(
      song.cloudinaryPublicId,
      song.cloudinaryResourceType
    );
  } catch {
    streamUrl = null;
  }
  return {
    ...base,
    streamUrl,
    isFavourite: favouriteIds.has(song.id),
  };
}

export async function listSongs(req: Request, res: Response): Promise<void> {
  const { page, limit, sortBy, order, genre, artistId, albumId } =
    paginationQuerySchema.parse(req.query);

  const where = {
    isPublished: true,
    ...(genre ? { genre: { equals: genre, mode: 'insensitive' as const } } : {}),
    ...(artistId ? { artistId } : {}),
    ...(albumId ? { albumId } : {}),
  };

  const skip = (page - 1) * limit;
  const [songs, total, favouriteIds] = await Promise.all([
    prisma.song.findMany({
      where,
      skip,
      take: limit,
      orderBy: { [sortBy]: order },
      include: {
        artist: { select: { id: true, name: true, artworkUrl: true } },
        album: { select: { id: true, title: true, artworkUrl: true } },
      },
    }),
    prisma.song.count({ where }),
    getUserFavouriteIds(req.user?.id),
  ]);

  res.json({
    success: true,
    data: {
      items: songs.map((s) => attachStreamAndFav(s, favouriteIds)),
      pagination: {
        page,
        limit,
        total,
        totalPages: Math.ceil(total / limit),
      },
    },
  });
}

export async function getSongById(req: Request, res: Response): Promise<void> {
  const id = req.params.id as string;
  const song = await prisma.song.findFirst({
    where: { id, isPublished: true },
    include: {
      artist: { select: { id: true, name: true, artworkUrl: true } },
      album: { select: { id: true, title: true, artworkUrl: true } },
    },
  });

  if (!song) {
    throw new AppError('Song not found', 404, 'SONG_NOT_FOUND');
  }

  const favouriteIds = await getUserFavouriteIds(req.user?.id);

  res.json({
    success: true,
    data: { song: attachStreamAndFav(song, favouriteIds) },
  });
}

export async function streamSong(req: Request, res: Response): Promise<void> {
  const id = req.params.id as string;
  const song = await prisma.song.findFirst({
    where: { id, isPublished: true },
  });

  if (!song) {
    throw new AppError('Song not found or unpublished', 404, 'SONG_NOT_FOUND');
  }

  const streamUrl = cloudinaryService.getAudioDeliveryUrl(
    song.cloudinaryPublicId,
    song.cloudinaryResourceType
  );

  // Increment play count asynchronously without blocking stream response
  prisma.song
    .update({
      where: { id: song.id },
      data: { playCount: { increment: 1 } },
    })
    .catch(() => {});

  if (req.query.redirect === 'true') {
    res.redirect(302, streamUrl);
    return;
  }

  res.json({
    success: true,
    data: {
      songId: song.id,
      streamUrl,
      publicId: song.cloudinaryPublicId,
      format: song.audioFormat,
      durationSeconds: song.durationSeconds,
      supportsRangeRequests: true,
    },
  });
}

export async function listAlbums(req: Request, res: Response): Promise<void> {
  const { page, limit, order, genre, artistId } = paginationQuerySchema.parse(req.query);
  const where = {
    ...(genre ? { genre: { equals: genre, mode: 'insensitive' as const } } : {}),
    ...(artistId ? { artistId } : {}),
  };

  const skip = (page - 1) * limit;
  const [albums, total] = await Promise.all([
    prisma.album.findMany({
      where,
      skip,
      take: limit,
      orderBy: { createdAt: order },
      include: {
        artist: { select: { id: true, name: true } },
        _count: { select: { songs: true } },
      },
    }),
    prisma.album.count({ where }),
  ]);

  res.json({
    success: true,
    data: {
      items: albums.map((a) => ({
        id: a.id,
        title: a.title,
        artistId: a.artistId,
        artistName: a.artist.name,
        artworkUrl: a.artworkUrl,
        releaseDate: a.releaseDate?.toISOString() ?? null,
        year: a.releaseDate ? a.releaseDate.getUTCFullYear() : 2024,
        genre: a.genre,
        songCount: a._count.songs,
      })),
      pagination: {
        page,
        limit,
        total,
        totalPages: Math.ceil(total / limit),
      },
    },
  });
}

export async function getAlbumById(req: Request, res: Response): Promise<void> {
  const id = req.params.id as string;
  const album = await prisma.album.findUnique({
    where: { id },
    include: {
      artist: { select: { id: true, name: true, artworkUrl: true } },
      songs: {
        where: { isPublished: true },
        orderBy: { createdAt: 'asc' },
        include: {
          artist: { select: { id: true, name: true, artworkUrl: true } },
          album: { select: { id: true, title: true, artworkUrl: true } },
        },
      },
    },
  });

  if (!album) {
    throw new AppError('Album not found', 404, 'ALBUM_NOT_FOUND');
  }

  const favouriteIds = await getUserFavouriteIds(req.user?.id);

  res.json({
    success: true,
    data: {
      album: {
        id: album.id,
        title: album.title,
        artistId: album.artistId,
        artistName: album.artist.name,
        artworkUrl: album.artworkUrl,
        releaseDate: album.releaseDate?.toISOString() ?? null,
        year: album.releaseDate ? album.releaseDate.getUTCFullYear() : 2024,
        genre: album.genre,
        songCount: album.songs.length,
        songs: album.songs.map((s) => attachStreamAndFav(s, favouriteIds)),
      },
    },
  });
}

export async function listArtists(req: Request, res: Response): Promise<void> {
  const { page, limit, order } = paginationQuerySchema.parse(req.query);
  const skip = (page - 1) * limit;

  const [artists, total] = await Promise.all([
    prisma.artist.findMany({
      skip,
      take: limit,
      orderBy: { createdAt: order },
    }),
    prisma.artist.count(),
  ]);

  res.json({
    success: true,
    data: {
      items: artists.map((a) => ({
        id: a.id,
        name: a.name,
        biography: a.biography,
        artworkUrl: a.artworkUrl,
        monthlyListeners: a.monthlyListeners,
      })),
      pagination: {
        page,
        limit,
        total,
        totalPages: Math.ceil(total / limit),
      },
    },
  });
}

export async function getArtistById(req: Request, res: Response): Promise<void> {
  const id = req.params.id as string;
  const artist = await prisma.artist.findUnique({
    where: { id },
    include: {
      songs: {
        where: { isPublished: true },
        orderBy: { playCount: 'desc' },
        take: 20,
        include: {
          artist: { select: { id: true, name: true, artworkUrl: true } },
          album: { select: { id: true, title: true, artworkUrl: true } },
        },
      },
      albums: {
        orderBy: { createdAt: 'desc' },
        include: { _count: { select: { songs: true } } },
      },
    },
  });

  if (!artist) {
    throw new AppError('Artist not found', 404, 'ARTIST_NOT_FOUND');
  }

  const favouriteIds = await getUserFavouriteIds(req.user?.id);

  res.json({
    success: true,
    data: {
      artist: {
        id: artist.id,
        name: artist.name,
        biography: artist.biography,
        artworkUrl: artist.artworkUrl,
        monthlyListeners: artist.monthlyListeners,
        popularSongs: artist.songs.map((s) => attachStreamAndFav(s, favouriteIds)),
        albums: artist.albums.map((a) => ({
          id: a.id,
          title: a.title,
          artistId: a.artistId,
          artistName: artist.name,
          artworkUrl: a.artworkUrl,
          year: a.releaseDate ? a.releaseDate.getUTCFullYear() : 2024,
          genre: a.genre,
          songCount: a._count.songs,
        })),
      },
    },
  });
}

export async function searchCatalog(req: Request, res: Response): Promise<void> {
  const { q, genre, artistId, limit } = searchQuerySchema.parse(req.query);

  const [songs, albums, artists, favouriteIds] = await Promise.all([
    prisma.song.findMany({
      where: {
        isPublished: true,
        ...(genre ? { genre: { equals: genre, mode: 'insensitive' } } : {}),
        ...(artistId ? { artistId } : {}),
        OR: [
          { title: { contains: q, mode: 'insensitive' } },
          { genre: { contains: q, mode: 'insensitive' } },
          { artist: { name: { contains: q, mode: 'insensitive' } } },
        ],
      },
      take: limit,
      include: {
        artist: { select: { id: true, name: true, artworkUrl: true } },
        album: { select: { id: true, title: true, artworkUrl: true } },
      },
    }),
    prisma.album.findMany({
      where: {
        ...(genre ? { genre: { equals: genre, mode: 'insensitive' } } : {}),
        ...(artistId ? { artistId } : {}),
        OR: [
          { title: { contains: q, mode: 'insensitive' } },
          { artist: { name: { contains: q, mode: 'insensitive' } } },
        ],
      },
      take: limit,
      include: {
        artist: { select: { id: true, name: true } },
        _count: { select: { songs: true } },
      },
    }),
    prisma.artist.findMany({
      where: {
        ...(artistId ? { id: artistId } : {}),
        name: { contains: q, mode: 'insensitive' },
      },
      take: limit,
    }),
    getUserFavouriteIds(req.user?.id),
  ]);

  res.json({
    success: true,
    data: {
      songs: songs.map((s) => attachStreamAndFav(s, favouriteIds)),
      albums: albums.map((a) => ({
        id: a.id,
        title: a.title,
        artistId: a.artistId,
        artistName: a.artist.name,
        artworkUrl: a.artworkUrl,
        year: a.releaseDate ? a.releaseDate.getUTCFullYear() : 2024,
        genre: a.genre,
        songCount: a._count.songs,
      })),
      artists: artists.map((a) => ({
        id: a.id,
        name: a.name,
        biography: a.biography,
        artworkUrl: a.artworkUrl,
        monthlyListeners: a.monthlyListeners,
      })),
    },
  });
}
