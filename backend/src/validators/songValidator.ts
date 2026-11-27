import { z } from 'zod';

export const uploadSongMetadataSchema = z.object({
  title: z.string().trim().min(1, 'Song title is required').max(200),
  artistId: z.string().uuid('artistId must be a valid UUID').optional(),
  artistName: z.string().trim().min(1).max(150).optional(),
  albumId: z.string().uuid('albumId must be a valid UUID').optional(),
  albumTitle: z.string().trim().min(1).max(200).optional(),
  genre: z.string().trim().min(1).max(80).default('Various'),
  artworkUrl: z.string().url('artworkUrl must be a valid URL').optional(),
  releaseDate: z.string().datetime().optional(),
  durationSeconds: z.coerce.number().int().nonnegative().optional(),
  isPublished: z
    .union([z.boolean(), z.string().transform((v) => v === 'true')])
    .optional()
    .default(true),
});

export const updateSongSchema = z.object({
  title: z.string().trim().min(1).max(200).optional(),
  artistId: z.string().uuid().optional(),
  albumId: z.string().uuid().nullable().optional(),
  genre: z.string().trim().min(1).max(80).optional(),
  artworkUrl: z.string().url().nullable().optional(),
  releaseDate: z.string().datetime().nullable().optional(),
  durationSeconds: z.coerce.number().int().nonnegative().optional(),
  isPublished: z.boolean().optional(),
});

export const publishSongSchema = z.object({
  isPublished: z.boolean().optional().default(true),
});
