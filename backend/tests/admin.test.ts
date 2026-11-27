import { describe, expect, it, vi, beforeEach } from 'vitest';
import request from 'supertest';

vi.mock('../src/config/prisma', () => {
  return {
    prisma: {
      song: {
        findUnique: vi.fn(),
        findFirst: vi.fn(),
        create: vi.fn(),
        update: vi.fn(),
        delete: vi.fn(),
      },
      artist: {
        findUnique: vi.fn(),
        findFirst: vi.fn(),
        create: vi.fn(),
      },
      album: {
        findFirst: vi.fn(),
        create: vi.fn(),
      },
    },
  };
});

vi.mock('../src/services/cloudinaryService', () => {
  return {
    cloudinaryService: {
      deleteAudioAsset: vi.fn().mockResolvedValue(undefined),
      getAudioDeliveryUrl: vi.fn().mockReturnValue('https://res.cloudinary.com/test/video.mp3'),
      uploadAudioFromPath: vi.fn().mockResolvedValue({
        publicId: 'vibe/music/new_test_track',
        resourceType: 'video',
        format: 'mp3',
        durationSeconds: 195,
        bytes: 4000000,
        bitrate: 320000,
        secureUrl: 'https://res.cloudinary.com/test/video.mp3',
      }),
    },
  };
});

import app from '../src/app';
import { prisma } from '../src/config/prisma';
import { cloudinaryService } from '../src/services/cloudinaryService';
import { signAuthToken } from '../src/middleware/authMiddleware';

describe('Admin API (/api/v1/admin)', () => {
  const adminToken = signAuthToken({
    id: 'adm-1',
    email: 'admin@vibe.music',
    role: 'ADMIN',
  });

  const userToken = signAuthToken({
    id: 'usr-1',
    email: 'user@vibe.music',
    role: 'USER',
  });

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('rejects regular users with 403 FORBIDDEN on admin routes', async () => {
    const res = await request(app)
      .delete('/api/v1/admin/songs/song-123')
      .set('Authorization', `Bearer ${userToken}`);

    expect(res.status).toBe(403);
    expect(res.body.error.code).toBe('ADMIN_ACCESS_REQUIRED');
  });

  it('rejects upload request without audio file', async () => {
    const res = await request(app)
      .post('/api/v1/admin/songs/upload')
      .set('Authorization', `Bearer ${adminToken}`)
      .field('title', 'Test Track')
      .field('artistName', 'Test Artist');

    expect(res.status).toBe(400);
    expect(res.body.error.code).toBe('MISSING_AUDIO_FILE');
  });

  it('rejects upload request with missing artist', async () => {
    const res = await request(app)
      .post('/api/v1/admin/songs/upload')
      .set('Authorization', `Bearer ${adminToken}`)
      .attach('audio', Buffer.from('fake audio data'), 'test.mp3')
      .field('title', 'Test Track');

    expect(res.status).toBe(400);
    expect(res.body.error.code).toBe('MISSING_ARTIST');
  });

  it('rejects duplicate song upload', async () => {
    (prisma.artist.findFirst as any).mockResolvedValue({ id: 'art-1', name: 'Existing Artist' });
    (prisma.song.findFirst as any).mockResolvedValue({ id: 'song-dup', title: 'Test Track' });

    const res = await request(app)
      .post('/api/v1/admin/songs/upload')
      .set('Authorization', `Bearer ${adminToken}`)
      .attach('audio', Buffer.from('fake audio data'), 'test.mp3')
      .field('title', 'Test Track')
      .field('artistName', 'Existing Artist');

    expect(res.status).toBe(409);
    expect(res.body.error.code).toBe('DUPLICATE_SONG');
  });

  it('rolls back Cloudinary asset when database insertion fails after upload', async () => {
    (prisma.artist.findFirst as any).mockResolvedValue({ id: 'art-1', name: 'Artist' });
    (prisma.song.findFirst as any).mockResolvedValue(null);
    (prisma.song.create as any).mockRejectedValue(new Error('DB connection dropped'));

    const res = await request(app)
      .post('/api/v1/admin/songs/upload')
      .set('Authorization', `Bearer ${adminToken}`)
      .attach('audio', Buffer.from('fake audio data'), 'test.mp3')
      .field('title', 'Test Track')
      .field('artistName', 'Artist');

    expect(res.status).toBe(500);
    expect(cloudinaryService.deleteAudioAsset).toHaveBeenCalledWith('vibe/music/new_test_track', 'video');
  });

  it('allows administrators to update song metadata', async () => {
    (prisma.song.findUnique as any).mockResolvedValue({ id: 'song-123' });
    (prisma.song.update as any).mockResolvedValue({
      id: 'song-123',
      title: 'Updated Title',
      artistId: 'art-1',
      albumId: null,
      cloudinaryPublicId: 'vibe/music/track_1',
      cloudinaryResourceType: 'video',
      originalFilename: 'track.mp3',
      audioFormat: 'mp3',
      durationSeconds: 180,
      fileSizeBytes: BigInt(4000000),
      bitrate: 320000,
      artworkUrl: null,
      genre: 'Chill',
      releaseDate: null,
      isPublished: true,
      playCount: BigInt(5),
      createdAt: new Date(),
      updatedAt: new Date(),
      artist: { id: 'art-1', name: 'Artist', artworkUrl: null },
      album: null,
    });

    const res = await request(app)
      .patch('/api/v1/admin/songs/song-123')
      .set('Authorization', `Bearer ${adminToken}`)
      .send({ title: 'Updated Title', genre: 'Chill' });

    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(res.body.data.song.title).toBe('Updated Title');
  });

  it('returns 404 when updating nonexistent song', async () => {
    (prisma.song.findUnique as any).mockResolvedValue(null);

    const res = await request(app)
      .patch('/api/v1/admin/songs/nonexistent-song')
      .set('Authorization', `Bearer ${adminToken}`)
      .send({ title: 'New Title' });

    expect(res.status).toBe(404);
    expect(res.body.error.code).toBe('SONG_NOT_FOUND');
  });

  it('allows administrators to delete a song and calls Cloudinary cleanup', async () => {
    (prisma.song.findUnique as any).mockResolvedValue({
      id: 'song-123',
      cloudinaryPublicId: 'vibe/music/track_to_delete',
      cloudinaryResourceType: 'video',
    });
    (prisma.song.delete as any).mockResolvedValue({});

    const res = await request(app)
      .delete('/api/v1/admin/songs/song-123')
      .set('Authorization', `Bearer ${adminToken}`);

    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(prisma.song.delete).toHaveBeenCalledWith({ where: { id: 'song-123' } });
  });

  it('allows administrators to publish or unpublish a song', async () => {
    (prisma.song.findUnique as any).mockResolvedValue({
      id: 'song-123',
    });
    (prisma.song.update as any).mockResolvedValue({
      id: 'song-123',
      title: 'Published Track',
      artistId: 'art-1',
      albumId: null,
      cloudinaryPublicId: 'vibe/music/pub_track',
      cloudinaryResourceType: 'video',
      originalFilename: 'track.mp3',
      audioFormat: 'mp3',
      durationSeconds: 150,
      fileSizeBytes: BigInt(3000000),
      bitrate: 256000,
      artworkUrl: null,
      genre: 'Tamil',
      releaseDate: null,
      isPublished: true,
      playCount: BigInt(0),
      createdAt: new Date(),
      updatedAt: new Date(),
      artist: { id: 'art-1', name: 'Anirudh', artworkUrl: null },
      album: null,
    });

    const res = await request(app)
      .post('/api/v1/admin/songs/song-123/publish')
      .set('Authorization', `Bearer ${adminToken}`)
      .send({ isPublished: true });

    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(res.body.data.song.isPublished).toBe(true);
  });
});
