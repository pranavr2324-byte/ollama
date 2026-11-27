import { Request, Response } from 'express';
import { prisma } from '../config/prisma';
import { cleanupTempFile } from '../middleware/uploadMiddleware';
import { cloudinaryService } from '../services/cloudinaryService';
import { AppError } from '../utils/AppError';
import { toPlainNumber } from '../utils/serialize';
import {
  publishSongSchema,
  updateSongSchema,
  uploadSongMetadataSchema,
} from '../validators/songValidator';

export function formatSongResponse(song: {
  id: string;
  title: string;
  artistId: string;
  albumId: string | null;
  cloudinaryPublicId: string;
  cloudinaryResourceType: string;
  originalFilename: string | null;
  audioFormat: string | null;
  durationSeconds: number;
  fileSizeBytes: bigint | null;
  bitrate: number | null;
  artworkUrl: string | null;
  genre: string;
  releaseDate: Date | null;
  isPublished: boolean;
  playCount: bigint;
  createdAt: Date;
  updatedAt: Date;
  artist?: { id: string; name: string; artworkUrl: string | null };
  album?: { id: string; title: string; artworkUrl: string | null } | null;
}) {
  return {
    id: song.id,
    title: song.title,
    artistId: song.artistId,
    artistName: song.artist?.name ?? 'Unknown Artist',
    albumId: song.albumId,
    albumTitle: song.album?.title ?? 'Single',
    cloudinaryPublicId: song.cloudinaryPublicId,
    cloudinaryResourceType: song.cloudinaryResourceType,
    originalFilename: song.originalFilename,
    audioFormat: song.audioFormat,
    durationSeconds: song.durationSeconds,
    durationMs: song.durationSeconds * 1000,
    fileSizeBytes: toPlainNumber(song.fileSizeBytes),
    bitrate: song.bitrate,
    artworkUrl: song.artworkUrl ?? song.album?.artworkUrl ?? song.artist?.artworkUrl ?? null,
    genre: song.genre,
    releaseDate: song.releaseDate?.toISOString() ?? null,
    isPublished: song.isPublished,
    playCount: toPlainNumber(song.playCount) ?? 0,
    createdAt: song.createdAt.toISOString(),
    updatedAt: song.updatedAt.toISOString(),
  };
}

export async function uploadSong(req: Request, res: Response): Promise<void> {
  const file = req.file;
  if (!file) {
    throw new AppError('Audio file is required under field "audio"', 400, 'MISSING_AUDIO_FILE');
  }

  let uploadedPublicId: string | null = null;
  let uploadedResourceType = 'video';

  try {
    const metadata = uploadSongMetadataSchema.parse(req.body);

    // Resolve or create artist
    let artistId = metadata.artistId;
    if (!artistId) {
      if (!metadata.artistName) {
        throw new AppError(
          'Either artistId or artistName must be provided',
          400,
          'MISSING_ARTIST'
        );
      }
      const existingArtist = await prisma.artist.findFirst({
        where: { name: { equals: metadata.artistName, mode: 'insensitive' } },
      });
      if (existingArtist) {
        artistId = existingArtist.id;
      } else {
        const createdArtist = await prisma.artist.create({
          data: {
            name: metadata.artistName,
            artworkUrl: metadata.artworkUrl ?? null,
          },
        });
        artistId = createdArtist.id;
      }
    } else {
      const artistExists = await prisma.artist.findUnique({ where: { id: artistId } });
      if (!artistExists) {
        throw new AppError('Artist not found', 404, 'ARTIST_NOT_FOUND');
      }
    }

    // Duplicate detection by title + artistId or originalFilename + fileSizeBytes
    const duplicate = await prisma.song.findFirst({
      where: {
        OR: [
          {
            title: { equals: metadata.title, mode: 'insensitive' },
            artistId,
          },
          {
            originalFilename: file.originalname,
            fileSizeBytes: BigInt(file.size),
          },
        ],
      },
    });

    if (duplicate) {
      throw new AppError(
        `Duplicate song detected: "${duplicate.title}" already exists`,
        409,
        'DUPLICATE_SONG'
      );
    }

    // Resolve optional album
    let albumId: string | null = metadata.albumId ?? null;
    if (!albumId && metadata.albumTitle) {
      const existingAlbum = await prisma.album.findFirst({
        where: {
          title: { equals: metadata.albumTitle, mode: 'insensitive' },
          artistId,
        },
      });
      if (existingAlbum) {
        albumId = existingAlbum.id;
      } else {
        const createdAlbum = await prisma.album.create({
          data: {
            title: metadata.albumTitle,
            artistId,
            artworkUrl: metadata.artworkUrl ?? null,
            genre: metadata.genre,
            releaseDate: metadata.releaseDate ? new Date(metadata.releaseDate) : null,
          },
        });
        albumId = createdAlbum.id;
      }
    }

    // Stream file to Cloudinary
    const cloudResult = await cloudinaryService.uploadAudioFromPath(file.path, {
      filename: file.originalname,
    });
    uploadedPublicId = cloudResult.publicId;
    uploadedResourceType = cloudResult.resourceType;

    // Persist record in PostgreSQL
    const song = await prisma.song.create({
      data: {
        title: metadata.title,
        artistId,
        albumId,
        cloudinaryPublicId: cloudResult.publicId,
        cloudinaryResourceType: cloudResult.resourceType,
        originalFilename: file.originalname,
        audioFormat: cloudResult.format,
        durationSeconds:
          cloudResult.durationSeconds > 0
            ? cloudResult.durationSeconds
            : (metadata.durationSeconds ?? 0),
        fileSizeBytes: BigInt(cloudResult.bytes || file.size),
        bitrate: cloudResult.bitrate,
        artworkUrl: metadata.artworkUrl ?? null,
        genre: metadata.genre,
        releaseDate: metadata.releaseDate ? new Date(metadata.releaseDate) : null,
        isPublished: metadata.isPublished,
      },
      include: {
        artist: { select: { id: true, name: true, artworkUrl: true } },
        album: { select: { id: true, title: true, artworkUrl: true } },
      },
    });

    res.status(201).json({
      success: true,
      data: {
        song: formatSongResponse(song),
      },
    });
  } catch (err) {
    // Cleanup orphaned cloud file if DB insertion or post-upload step failed
    if (uploadedPublicId) {
      try {
        await cloudinaryService.deleteAudioAsset(uploadedPublicId, uploadedResourceType);
      } catch (cleanupErr) {
        console.error('Failed to cleanup orphaned Cloudinary asset:', cleanupErr);
      }
    }
    throw err;
  } finally {
    await cleanupTempFile(file.path);
  }
}

export async function updateSong(req: Request, res: Response): Promise<void> {
  const id = req.params.id as string;
  const payload = updateSongSchema.parse(req.body);

  const existing = await prisma.song.findUnique({ where: { id } });
  if (!existing) {
    throw new AppError('Song not found', 404, 'SONG_NOT_FOUND');
  }

  const updated = await prisma.song.update({
    where: { id },
    data: {
      ...(payload.title !== undefined ? { title: payload.title } : {}),
      ...(payload.artistId !== undefined ? { artistId: payload.artistId } : {}),
      ...(payload.albumId !== undefined ? { albumId: payload.albumId } : {}),
      ...(payload.genre !== undefined ? { genre: payload.genre } : {}),
      ...(payload.artworkUrl !== undefined ? { artworkUrl: payload.artworkUrl } : {}),
      ...(payload.releaseDate !== undefined
        ? { releaseDate: payload.releaseDate ? new Date(payload.releaseDate) : null }
        : {}),
      ...(payload.durationSeconds !== undefined ? { durationSeconds: payload.durationSeconds } : {}),
      ...(payload.isPublished !== undefined ? { isPublished: payload.isPublished } : {}),
    },
    include: {
      artist: { select: { id: true, name: true, artworkUrl: true } },
      album: { select: { id: true, title: true, artworkUrl: true } },
    },
  });

  res.json({
    success: true,
    data: { song: formatSongResponse(updated) },
  });
}

export async function deleteSong(req: Request, res: Response): Promise<void> {
  const id = req.params.id as string;
  const existing = await prisma.song.findUnique({ where: { id } });
  if (!existing) {
    throw new AppError('Song not found', 404, 'SONG_NOT_FOUND');
  }

  await prisma.song.delete({ where: { id } });
  await cloudinaryService.deleteAudioAsset(
    existing.cloudinaryPublicId,
    existing.cloudinaryResourceType
  );

  res.json({
    success: true,
    message: 'Song deleted from database and Cloudinary',
  });
}

export async function publishSong(req: Request, res: Response): Promise<void> {
  const id = req.params.id as string;
  const { isPublished } = publishSongSchema.parse(req.body ?? {});

  const existing = await prisma.song.findUnique({ where: { id } });
  if (!existing) {
    throw new AppError('Song not found', 404, 'SONG_NOT_FOUND');
  }

  const updated = await prisma.song.update({
    where: { id },
    data: { isPublished },
    include: {
      artist: { select: { id: true, name: true, artworkUrl: true } },
      album: { select: { id: true, title: true, artworkUrl: true } },
    },
  });

  res.json({
    success: true,
    data: { song: formatSongResponse(updated) },
  });
}
