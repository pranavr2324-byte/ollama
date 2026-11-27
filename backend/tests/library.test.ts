import { describe, expect, it, vi, beforeEach } from 'vitest';
import request from 'supertest';

vi.mock('../src/config/prisma', () => {
  return {
    prisma: {
      playlist: {
        findMany: vi.fn(),
        findUnique: vi.fn(),
        create: vi.fn(),
        update: vi.fn(),
        delete: vi.fn(),
      },
      playlistSong: {
        findFirst: vi.fn(),
        upsert: vi.fn(),
        deleteMany: vi.fn(),
      },
      song: {
        findUnique: vi.fn(),
        update: vi.fn(),
      },
      favourite: {
        findMany: vi.fn(),
        upsert: vi.fn(),
        deleteMany: vi.fn(),
      },
      recentlyPlayed: {
        findMany: vi.fn(),
        create: vi.fn(),
      },
    },
  };
});

import app from '../src/app';
import { prisma } from '../src/config/prisma';
import { signAuthToken } from '../src/middleware/authMiddleware';

describe('Library API (/api/v1)', () => {
  const token = signAuthToken({
    id: 'usr-123',
    email: 'user@vibe.music',
    role: 'USER',
    name: 'Vibe User',
  });

  const otherUserToken = signAuthToken({
    id: 'usr-other',
    email: 'other@vibe.music',
    role: 'USER',
    name: 'Other User',
  });

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Playlists', () => {
    it('creates a new playlist for the authenticated user', async () => {
      (prisma.playlist.create as any).mockResolvedValue({
        id: 'pl-1',
        userId: 'usr-123',
        name: 'My Chill Mix',
        description: 'Vibes for study',
        artworkUrl: null,
        createdAt: new Date(),
        updatedAt: new Date(),
      });

      const res = await request(app)
        .post('/api/v1/playlists')
        .set('Authorization', `Bearer ${token}`)
        .send({
          name: 'My Chill Mix',
          description: 'Vibes for study',
        });

      expect(res.status).toBe(201);
      expect(res.body.success).toBe(true);
      expect(res.body.data.playlist.name).toBe('My Chill Mix');
    });

    it('lists playlists owned by the authenticated user', async () => {
      (prisma.playlist.findMany as any).mockResolvedValue([
        {
          id: 'pl-1',
          name: 'My Chill Mix',
          description: 'Vibes for study',
          artworkUrl: null,
          createdAt: new Date(),
          updatedAt: new Date(),
          _count: { songs: 5 },
          songs: [],
        },
      ]);

      const res = await request(app)
        .get('/api/v1/playlists')
        .set('Authorization', `Bearer ${token}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.playlists).toHaveLength(1);
      expect(res.body.data.playlists[0].songCount).toBe(5);
    });

    it('updates playlist when user is owner', async () => {
      (prisma.playlist.findUnique as any).mockResolvedValue({
        id: 'pl-1',
        userId: 'usr-123',
        name: 'Old Name',
      });
      (prisma.playlist.update as any).mockResolvedValue({
        id: 'pl-1',
        name: 'New Name',
        description: 'Updated',
        artworkUrl: null,
        updatedAt: new Date(),
      });

      const res = await request(app)
        .patch('/api/v1/playlists/pl-1')
        .set('Authorization', `Bearer ${token}`)
        .send({ name: 'New Name' });

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.playlist.name).toBe('New Name');
    });

    it('rejects playlist update when user is not owner', async () => {
      (prisma.playlist.findUnique as any).mockResolvedValue({
        id: 'pl-1',
        userId: 'usr-123',
      });

      const res = await request(app)
        .patch('/api/v1/playlists/pl-1')
        .set('Authorization', `Bearer ${otherUserToken}`)
        .send({ name: 'Hacked Name' });

      expect(res.status).toBe(403);
      expect(res.body.error.code).toBe('FORBIDDEN');
    });

    it('deletes playlist when user is owner', async () => {
      (prisma.playlist.findUnique as any).mockResolvedValue({
        id: 'pl-1',
        userId: 'usr-123',
      });
      (prisma.playlist.delete as any).mockResolvedValue({});

      const res = await request(app)
        .delete('/api/v1/playlists/pl-1')
        .set('Authorization', `Bearer ${token}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(prisma.playlist.delete).toHaveBeenCalledWith({ where: { id: 'pl-1' } });
    });

    it('returns 404 when deleting nonexistent playlist', async () => {
      (prisma.playlist.findUnique as any).mockResolvedValue(null);

      const res = await request(app)
        .delete('/api/v1/playlists/missing-pl')
        .set('Authorization', `Bearer ${token}`);

      expect(res.status).toBe(404);
      expect(res.body.error.code).toBe('PLAYLIST_NOT_FOUND');
    });

    it('adds a song to a playlist', async () => {
      (prisma.playlist.findUnique as any).mockResolvedValue({
        id: 'pl-1',
        userId: 'usr-123',
      });
      (prisma.song.findUnique as any).mockResolvedValue({
        id: '123e4567-e89b-12d3-a456-426614174000',
        title: 'Song',
      });
      (prisma.playlistSong.findFirst as any).mockResolvedValue({ position: 2 });
      (prisma.playlistSong.upsert as any).mockResolvedValue({});
      (prisma.playlist.update as any).mockResolvedValue({});

      const res = await request(app)
        .post('/api/v1/playlists/pl-1/songs')
        .set('Authorization', `Bearer ${token}`)
        .send({ songId: '123e4567-e89b-12d3-a456-426614174000' });

      expect(res.status).toBe(201);
      expect(res.body.success).toBe(true);
      expect(res.body.data.position).toBe(3);
    });

    it('removes a song from a playlist', async () => {
      (prisma.playlist.findUnique as any).mockResolvedValue({
        id: 'pl-1',
        userId: 'usr-123',
      });
      (prisma.playlistSong.deleteMany as any).mockResolvedValue({ count: 1 });
      (prisma.playlist.update as any).mockResolvedValue({});

      const res = await request(app)
        .delete('/api/v1/playlists/pl-1/songs/123e4567-e89b-12d3-a456-426614174000')
        .set('Authorization', `Bearer ${token}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
    });
  });

  describe('Favourites', () => {
    it('adds a song to favourites and returns 201', async () => {
      (prisma.song.findUnique as any).mockResolvedValue({
        id: '123e4567-e89b-12d3-a456-426614174000',
        title: 'Song',
      });
      (prisma.favourite.upsert as any).mockResolvedValue({});

      const res = await request(app)
        .post('/api/v1/favourites/123e4567-e89b-12d3-a456-426614174000')
        .set('Authorization', `Bearer ${token}`);

      expect(res.status).toBe(201);
      expect(res.body.success).toBe(true);
      expect(res.body.data.isFavourite).toBe(true);
    });

    it('removes a song from favourites and returns 200', async () => {
      (prisma.favourite.deleteMany as any).mockResolvedValue({ count: 1 });

      const res = await request(app)
        .delete('/api/v1/favourites/123e4567-e89b-12d3-a456-426614174000')
        .set('Authorization', `Bearer ${token}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.isFavourite).toBe(false);
    });
  });

  describe('Recently Played', () => {
    it('records playback and increments play count', async () => {
      (prisma.song.findUnique as any).mockResolvedValue({
        id: '123e4567-e89b-12d3-a456-426614174000',
        title: 'Song',
      });
      (prisma.recentlyPlayed.create as any).mockResolvedValue({});
      (prisma.song.update as any).mockResolvedValue({});

      const res = await request(app)
        .post('/api/v1/recently-played/123e4567-e89b-12d3-a456-426614174000')
        .set('Authorization', `Bearer ${token}`);

      expect(res.status).toBe(201);
      expect(res.body.success).toBe(true);
      expect(prisma.song.update).toHaveBeenCalledWith({
        where: { id: '123e4567-e89b-12d3-a456-426614174000' },
        data: { playCount: { increment: 1 } },
      });
    });

    it('lists recently played songs for the authenticated user', async () => {
      (prisma.recentlyPlayed.findMany as any).mockResolvedValue([]);
      (prisma.favourite.findMany as any).mockResolvedValue([]);

      const res = await request(app)
        .get('/api/v1/recently-played')
        .set('Authorization', `Bearer ${token}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.items).toBeDefined();
    });
  });
});
