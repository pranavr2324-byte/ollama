import { z } from 'zod';

export const createPlaylistSchema = z.object({
  name: z.string().trim().min(1, 'Playlist name is required').max(150),
  description: z.string().trim().max(500).optional(),
  artworkUrl: z.string().url().optional(),
});

export const updatePlaylistSchema = z.object({
  name: z.string().trim().min(1).max(150).optional(),
  description: z.string().trim().max(500).nullable().optional(),
  artworkUrl: z.string().url().nullable().optional(),
});

export const addSongToPlaylistSchema = z.object({
  songId: z.string().uuid('songId must be a valid UUID'),
});
