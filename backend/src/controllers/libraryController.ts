import { Request, Response } from 'express';
import { prisma } from '../config/prisma';
import { cloudinaryService } from '../services/cloudinaryService';
import { AppError } from '../utils/AppError';
import {
  addSongToPlaylistSchema,
  createPlaylistSchema,
  updatePlaylistSchema,
} from '../validators/libraryValidator';
import { formatSongResponse } from './adminController';

function attachStreamAndFav(
  song: Parameters<typeof formatSongResponse>[0],
  isFavourite = true
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
    isFavourite,
  };
}

// ==========================================
// PLAYLISTS
// ==========================================

export async function listPlaylists(req: Request, res: Response): Promise<void> {
  const userId = req.user?.id;
  if (!userId) {
    throw new AppError('Authentication required', 401, 'UNAUTHORIZED');
  }

  const playlists = await prisma.playlist.findMany({
    where: { userId },
    orderBy: { updatedAt: 'desc' },
    include: {
      _count: { select: { songs: true } },
      songs: {
        take: 1,
        include: {
          song: {
            select: { artworkUrl: true },
          },
        },
      },
    },
  });

  res.json({
    success: true,
    data: {
      playlists: playlists.map((p) => ({
        id: p.id,
        name: p.name,
        description: p.description,
        artworkUrl: p.artworkUrl ?? p.songs[0]?.song.artworkUrl ?? null,
        songCount: p._count.songs,
        createdAt: p.createdAt.toISOString(),
        updatedAt: p.updatedAt.toISOString(),
      })),
    },
  });
}

export async function getPlaylistById(req: Request, res: Response): Promise<void> {
  const id = req.params.id as string;
  const userId = req.user?.id;

  const playlist = await prisma.playlist.findUnique({
    where: { id },
    include: {
      songs: {
        orderBy: { position: 'asc' },
        include: {
          song: {
            include: {
              artist: { select: { id: true, name: true, artworkUrl: true } },
              album: { select: { id: true, title: true, artworkUrl: true } },
            },
          },
        },
      },
    },
  });

  if (!playlist) {
    throw new AppError('Playlist not found', 404, 'PLAYLIST_NOT_FOUND');
  }

  // Check favourites for current user
  let favIds = new Set<string>();
  if (userId) {
    const favs = await prisma.favourite.findMany({
      where: { userId },
      select: { songId: true },
    });
    favIds = new Set(favs.map((f) => f.songId));
  }

  const songs = playlist.songs.map((ps) => ({
    ...attachStreamAndFav(ps.song, favIds.has(ps.song.id)),
    position: ps.position,
    addedAt: ps.addedAt.toISOString(),
  }));

  res.json({
    success: true,
    data: {
      playlist: {
        id: playlist.id,
        userId: playlist.userId,
        name: playlist.name,
        description: playlist.description,
        artworkUrl: playlist.artworkUrl ?? songs[0]?.artworkUrl ?? null,
        songCount: songs.length,
        createdAt: playlist.createdAt.toISOString(),
        updatedAt: playlist.updatedAt.toISOString(),
        songs,
      },
    },
  });
}

export async function createPlaylist(req: Request, res: Response): Promise<void> {
  const userId = req.user?.id;
  if (!userId) {
    throw new AppError('Authentication required', 401, 'UNAUTHORIZED');
  }

  const { name, description, artworkUrl } = createPlaylistSchema.parse(req.body);

  const playlist = await prisma.playlist.create({
    data: {
      userId,
      name,
      description: description ?? null,
      artworkUrl: artworkUrl ?? null,
    },
  });

  res.status(201).json({
    success: true,
    data: {
      playlist: {
        id: playlist.id,
        name: playlist.name,
        description: playlist.description,
        artworkUrl: playlist.artworkUrl,
        songCount: 0,
        createdAt: playlist.createdAt.toISOString(),
        updatedAt: playlist.updatedAt.toISOString(),
        songs: [],
      },
    },
  });
}

export async function updatePlaylist(req: Request, res: Response): Promise<void> {
  const id = req.params.id as string;
  const userId = req.user?.id;
  if (!userId) {
    throw new AppError('Authentication required', 401, 'UNAUTHORIZED');
  }

  const existing = await prisma.playlist.findUnique({ where: { id } });
  if (!existing) {
    throw new AppError('Playlist not found', 404, 'PLAYLIST_NOT_FOUND');
  }
  if (existing.userId !== userId && req.user?.role !== 'ADMIN') {
    throw new AppError('You do not have permission to modify this playlist', 403, 'FORBIDDEN');
  }

  const payload = updatePlaylistSchema.parse(req.body);

  const updated = await prisma.playlist.update({
    where: { id },
    data: {
      ...(payload.name !== undefined ? { name: payload.name } : {}),
      ...(payload.description !== undefined ? { description: payload.description } : {}),
      ...(payload.artworkUrl !== undefined ? { artworkUrl: payload.artworkUrl } : {}),
    },
  });

  res.json({
    success: true,
    data: {
      playlist: {
        id: updated.id,
        name: updated.name,
        description: updated.description,
        artworkUrl: updated.artworkUrl,
        updatedAt: updated.updatedAt.toISOString(),
      },
    },
  });
}

export async function deletePlaylist(req: Request, res: Response): Promise<void> {
  const id = req.params.id as string;
  const userId = req.user?.id;
  if (!userId) {
    throw new AppError('Authentication required', 401, 'UNAUTHORIZED');
  }

  const existing = await prisma.playlist.findUnique({ where: { id } });
  if (!existing) {
    throw new AppError('Playlist not found', 404, 'PLAYLIST_NOT_FOUND');
  }
  if (existing.userId !== userId && req.user?.role !== 'ADMIN') {
    throw new AppError('You do not have permission to delete this playlist', 403, 'FORBIDDEN');
  }

  await prisma.playlist.delete({ where: { id } });

  res.json({
    success: true,
    message: 'Playlist deleted successfully',
  });
}

export async function addSongToPlaylist(req: Request, res: Response): Promise<void> {
  const id = req.params.id as string;
  const userId = req.user?.id;
  if (!userId) {
    throw new AppError('Authentication required', 401, 'UNAUTHORIZED');
  }

  const { songId } = addSongToPlaylistSchema.parse(req.body);

  const playlist = await prisma.playlist.findUnique({ where: { id } });
  if (!playlist) {
    throw new AppError('Playlist not found', 404, 'PLAYLIST_NOT_FOUND');
  }
  if (playlist.userId !== userId && req.user?.role !== 'ADMIN') {
    throw new AppError('You do not have permission to modify this playlist', 403, 'FORBIDDEN');
  }

  const song = await prisma.song.findUnique({ where: { id: songId } });
  if (!song) {
    throw new AppError('Song not found', 404, 'SONG_NOT_FOUND');
  }

  // Get max position in playlist
  const lastSong = await prisma.playlistSong.findFirst({
    where: { playlistId: id },
    orderBy: { position: 'desc' },
  });
  const nextPosition = (lastSong?.position ?? 0) + 1;

  await prisma.playlistSong.upsert({
    where: {
      playlistId_songId: { playlistId: id, songId },
    },
    update: {},
    create: {
      playlistId: id,
      songId,
      position: nextPosition,
    },
  });

  await prisma.playlist.update({
    where: { id },
    data: { updatedAt: new Date() },
  });

  res.status(201).json({
    success: true,
    message: 'Song added to playlist',
    data: { playlistId: id, songId, position: nextPosition },
  });
}

export async function removeSongFromPlaylist(req: Request, res: Response): Promise<void> {
  const id = req.params.id as string;
  const songId = req.params.songId as string;
  const userId = req.user?.id;
  if (!userId) {
    throw new AppError('Authentication required', 401, 'UNAUTHORIZED');
  }

  const playlist = await prisma.playlist.findUnique({ where: { id } });
  if (!playlist) {
    throw new AppError('Playlist not found', 404, 'PLAYLIST_NOT_FOUND');
  }
  if (playlist.userId !== userId && req.user?.role !== 'ADMIN') {
    throw new AppError('You do not have permission to modify this playlist', 403, 'FORBIDDEN');
  }

  await prisma.playlistSong.deleteMany({
    where: { playlistId: id, songId },
  });

  await prisma.playlist.update({
    where: { id },
    data: { updatedAt: new Date() },
  });

  res.json({
    success: true,
    message: 'Song removed from playlist',
  });
}

// ==========================================
// FAVOURITES
// ==========================================

export async function listFavourites(req: Request, res: Response): Promise<void> {
  const userId = req.user?.id;
  if (!userId) {
    throw new AppError('Authentication required', 401, 'UNAUTHORIZED');
  }

  const favourites = await prisma.favourite.findMany({
    where: { userId },
    orderBy: { createdAt: 'desc' },
    include: {
      song: {
        include: {
          artist: { select: { id: true, name: true, artworkUrl: true } },
          album: { select: { id: true, title: true, artworkUrl: true } },
        },
      },
    },
  });

  res.json({
    success: true,
    data: {
      songs: favourites.map((f) => attachStreamAndFav(f.song, true)),
      total: favourites.length,
    },
  });
}

export async function addFavourite(req: Request, res: Response): Promise<void> {
  const songId = req.params.songId as string;
  const userId = req.user?.id;
  if (!userId) {
    throw new AppError('Authentication required', 401, 'UNAUTHORIZED');
  }

  const song = await prisma.song.findUnique({ where: { id: songId } });
  if (!song) {
    throw new AppError('Song not found', 404, 'SONG_NOT_FOUND');
  }

  await prisma.favourite.upsert({
    where: {
      userId_songId: { userId, songId },
    },
    update: {},
    create: {
      userId,
      songId,
    },
  });

  res.status(201).json({
    success: true,
    message: 'Song added to favourites',
    data: { songId, isFavourite: true },
  });
}

export async function removeFavourite(req: Request, res: Response): Promise<void> {
  const songId = req.params.songId as string;
  const userId = req.user?.id;
  if (!userId) {
    throw new AppError('Authentication required', 401, 'UNAUTHORIZED');
  }

  await prisma.favourite.deleteMany({
    where: { userId, songId },
  });

  res.json({
    success: true,
    message: 'Song removed from favourites',
    data: { songId, isFavourite: false },
  });
}

// ==========================================
// RECENTLY PLAYED
// ==========================================

export async function listRecentlyPlayed(req: Request, res: Response): Promise<void> {
  const userId = req.user?.id;
  if (!userId) {
    throw new AppError('Authentication required', 401, 'UNAUTHORIZED');
  }

  const [recent, favs] = await Promise.all([
    prisma.recentlyPlayed.findMany({
      where: { userId },
      orderBy: { playedAt: 'desc' },
      take: 30,
      include: {
        song: {
          include: {
            artist: { select: { id: true, name: true, artworkUrl: true } },
            album: { select: { id: true, title: true, artworkUrl: true } },
          },
        },
      },
    }),
    prisma.favourite.findMany({
      where: { userId },
      select: { songId: true },
    }),
  ]);

  const favIds = new Set(favs.map((f) => f.songId));

  res.json({
    success: true,
    data: {
      items: recent.map((r) => ({
        ...attachStreamAndFav(r.song, favIds.has(r.song.id)),
        playedAt: r.playedAt.toISOString(),
      })),
      total: recent.length,
    },
  });
}

export async function recordRecentlyPlayed(req: Request, res: Response): Promise<void> {
  const songId = req.params.songId as string;
  const userId = req.user?.id;
  if (!userId) {
    throw new AppError('Authentication required', 401, 'UNAUTHORIZED');
  }

  const song = await prisma.song.findUnique({ where: { id: songId } });
  if (!song) {
    throw new AppError('Song not found', 404, 'SONG_NOT_FOUND');
  }

  await Promise.all([
    prisma.recentlyPlayed.create({
      data: {
        userId,
        songId,
        playedAt: new Date(),
      },
    }),
    prisma.song.update({
      where: { id: songId },
      data: { playCount: { increment: 1 } },
    }),
  ]);

  res.status(201).json({
    success: true,
    message: 'Playback recorded',
  });
}
