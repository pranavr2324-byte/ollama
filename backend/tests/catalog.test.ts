import { describe, expect, it, vi, beforeEach } from 'vitest';
import request from 'supertest';

vi.mock('../src/config/prisma', () => {
  return {
    prisma: {
      song: {
        findMany: vi.fn(),
        findFirst: vi.fn(),
        count: vi.fn(),
        update: vi.fn(),
      },
      album: {
        findMany: vi.fn(),
        findUnique: vi.fn(),
        count: vi.fn(),
      },
      artist: {
        findMany: vi.fn(),
        findUnique: vi.fn(),
        count: vi.fn(),
      },
      favourite: {
        findMany: vi.fn().mockResolvedValue([]),
      },
    },
  };
});

import app from '../src/app';
import { prisma } from '../src/config/prisma';

describe('Catalog API (/api/v1)', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('GET /api/v1/songs', () => {
    it('returns a paginated list of published songs with streamUrls', async () => {
      const mockSong = {
        id: 'c56a4180-65aa-42ec-a945-5fd21dec0538',
        title: 'Tamil Chill Melody',
        artistId: 'art-1',
        albumId: 'alb-1',
        cloudinaryPublicId: 'vibe/music/sample_chill_lofi',
        cloudinaryResourceType: 'video',
        originalFilename: 'sample_chill_lofi.wav',
        audioFormat: 'wav',
        durationSeconds: 180,
        fileSizeBytes: BigInt(5000000),
        bitrate: 320000,
        artworkUrl: 'https://images.unsplash.com/photo-1',
        genre: 'Tamil Pop',
        releaseDate: new Date('2024-01-01'),
        isPublished: true,
        playCount: BigInt(120),
        createdAt: new Date(),
        updatedAt: new Date(),
        artist: { id: 'art-1', name: 'Anirudh', artworkUrl: 'https://art.jpg' },
        album: { id: 'alb-1', title: 'Midnight Chill', artworkUrl: 'https://alb.jpg' },
      };

      (prisma.song.findMany as any).mockResolvedValue([mockSong]);
      (prisma.song.count as any).mockResolvedValue(1);
      (prisma.favourite.findMany as any).mockResolvedValue([]);

      const res = await request(app).get('/api/v1/songs');

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.items).toHaveLength(1);
      expect(res.body.data.items[0].title).toBe('Tamil Chill Melody');
      expect(res.body.data.items[0].streamUrl).toContain('vibe/music/sample_chill_lofi');
    });

    it('returns 404 when song ID is not found', async () => {
      (prisma.song.findFirst as any).mockResolvedValue(null);

      const res = await request(app).get('/api/v1/songs/c56a4180-65aa-42ec-a945-5fd21dec0538');

      expect(res.status).toBe(404);
      expect(res.body.error.code).toBe('SONG_NOT_FOUND');
    });
  });

  describe('GET /api/v1/songs/:id/stream', () => {
    it('returns stream JSON with delivery URL and audio metadata', async () => {
      const mockSong = {
        id: 'c56a4180-65aa-42ec-a945-5fd21dec0538',
        cloudinaryPublicId: 'vibe/music/sample_tamil_melody',
        cloudinaryResourceType: 'video',
        audioFormat: 'wav',
        durationSeconds: 210,
        isPublished: true,
      };

      (prisma.song.findFirst as any).mockResolvedValue(mockSong);
      (prisma.song.update as any).mockResolvedValue(mockSong);

      const res = await request(app).get(`/api/v1/songs/${mockSong.id}/stream`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.streamUrl).toContain('vibe/music/sample_tamil_melody');
      expect(res.body.data.format).toBe('wav');
    });

    it('returns 404 when streaming unpublished or nonexistent song', async () => {
      (prisma.song.findFirst as any).mockResolvedValue(null);

      const res = await request(app).get('/api/v1/songs/missing-id/stream');

      expect(res.status).toBe(404);
      expect(res.body.error.code).toBe('SONG_NOT_FOUND');
    });

    it('redirects with 302 when redirect=true query parameter is supplied', async () => {
      const mockSong = {
        id: 'c56a4180-65aa-42ec-a945-5fd21dec0538',
        cloudinaryPublicId: 'vibe/music/sample_tamil_melody',
        cloudinaryResourceType: 'video',
        audioFormat: 'wav',
        durationSeconds: 210,
        isPublished: true,
      };

      (prisma.song.findFirst as any).mockResolvedValue(mockSong);
      (prisma.song.update as any).mockResolvedValue(mockSong);

      const res = await request(app).get(`/api/v1/songs/${mockSong.id}/stream?redirect=true`);

      expect(res.status).toBe(302);
      expect(res.headers.location).toContain('vibe/music/sample_tamil_melody');
    });
  });

  describe('GET /api/v1/albums/:id', () => {
    it('returns 404 when album is not found', async () => {
      (prisma.album.findUnique as any).mockResolvedValue(null);

      const res = await request(app).get('/api/v1/albums/missing-alb-id');

      expect(res.status).toBe(404);
      expect(res.body.error.code).toBe('ALBUM_NOT_FOUND');
    });
  });

  describe('GET /api/v1/artists/:id', () => {
    it('returns 404 when artist is not found', async () => {
      (prisma.artist.findUnique as any).mockResolvedValue(null);

      const res = await request(app).get('/api/v1/artists/missing-art-id');

      expect(res.status).toBe(404);
      expect(res.body.error.code).toBe('ARTIST_NOT_FOUND');
    });
  });

  describe('GET /api/v1/search', () => {
    it('searches across songs, albums, and artists', async () => {
      (prisma.song.findMany as any).mockResolvedValue([]);
      (prisma.album.findMany as any).mockResolvedValue([]);
      (prisma.artist.findMany as any).mockResolvedValue([]);
      (prisma.favourite.findMany as any).mockResolvedValue([]);

      const res = await request(app).get('/api/v1/search?q=tamil');

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.songs).toBeDefined();
      expect(res.body.data.albums).toBeDefined();
      expect(res.body.data.artists).toBeDefined();
    });
  });
});
