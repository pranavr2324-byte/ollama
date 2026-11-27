import { z } from 'zod';

export const paginationQuerySchema = z.object({
  page: z.coerce.number().int().min(1).default(1),
  limit: z.coerce.number().int().min(1).max(100).default(20),
  sortBy: z.enum(['createdAt', 'title', 'playCount', 'releaseDate']).default('createdAt'),
  order: z.enum(['asc', 'desc']).default('desc'),
  genre: z.string().trim().optional(),
  artistId: z.string().uuid().optional(),
  albumId: z.string().uuid().optional(),
});

export const searchQuerySchema = z.object({
  q: z.string().trim().min(1, 'Search query "q" is required').max(150),
  genre: z.string().trim().optional(),
  artistId: z.string().uuid().optional(),
  limit: z.coerce.number().int().min(1).max(50).default(20),
});
