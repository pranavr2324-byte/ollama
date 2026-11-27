import { describe, expect, it, vi, beforeEach } from 'vitest';
import request from 'supertest';
import bcrypt from 'bcryptjs';

vi.mock('../src/config/prisma', () => {
  return {
    prisma: {
      user: {
        findUnique: vi.fn(),
        create: vi.fn(),
      },
    },
  };
});

vi.mock('../src/config/supabase', () => ({
  getSupabaseClient: () => null,
  getSupabaseAdminClient: () => null,
}));

import app from '../src/app';
import { prisma } from '../src/config/prisma';
import { signAuthToken } from '../src/middleware/authMiddleware';

describe('Auth API (/api/v1/auth)', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('POST /api/v1/auth/register', () => {
    it('registers a new user successfully and returns a JWT token', async () => {
      (prisma.user.findUnique as any).mockResolvedValue(null);
      (prisma.user.create as any).mockResolvedValue({
        id: 'usr-123',
        email: 'test@vibe.music',
        name: 'Vibe Listener',
        role: 'USER',
      });

      const res = await request(app)
        .post('/api/v1/auth/register')
        .send({
          email: 'test@vibe.music',
          password: 'Password123!',
          name: 'Vibe Listener',
        });

      expect(res.status).toBe(201);
      expect(res.body.success).toBe(true);
      expect(res.body.data.token).toBeDefined();
      expect(res.body.data.user.email).toBe('test@vibe.music');
    });

    it('rejects registration if email is already in use', async () => {
      (prisma.user.findUnique as any).mockResolvedValue({
        id: 'existing-id',
        email: 'test@vibe.music',
      });

      const res = await request(app)
        .post('/api/v1/auth/register')
        .send({
          email: 'test@vibe.music',
          password: 'Password123!',
          name: 'Vibe Listener',
        });

      expect(res.status).toBe(409);
      expect(res.body.success).toBe(false);
      expect(res.body.error.code).toBe('EMAIL_EXISTS');
    });

    it('validates invalid email and weak password', async () => {
      const res = await request(app)
        .post('/api/v1/auth/register')
        .send({
          email: 'not-an-email',
          password: '123',
        });

      expect(res.status).toBe(400);
      expect(res.body.success).toBe(false);
    });
  });

  describe('POST /api/v1/auth/login', () => {
    it('authenticates user with valid credentials and returns token', async () => {
      const hash = await bcrypt.hash('CorrectPass123', 10);
      (prisma.user.findUnique as any).mockResolvedValue({
        id: 'usr-123',
        email: 'listener@vibe.music',
        passwordHash: hash,
        role: 'USER',
        name: 'Test Listener',
      });

      const res = await request(app)
        .post('/api/v1/auth/login')
        .send({
          email: 'listener@vibe.music',
          password: 'CorrectPass123',
        });

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.token).toBeDefined();
      expect(res.body.data.user.email).toBe('listener@vibe.music');
    });

    it('rejects login with invalid password', async () => {
      const hash = await bcrypt.hash('CorrectPass123', 10);
      (prisma.user.findUnique as any).mockResolvedValue({
        id: 'usr-123',
        email: 'listener@vibe.music',
        passwordHash: hash,
        role: 'USER',
      });

      const res = await request(app)
        .post('/api/v1/auth/login')
        .send({
          email: 'listener@vibe.music',
          password: 'WrongPassword',
        });

      expect(res.status).toBe(401);
      expect(res.body.success).toBe(false);
    });
  });

  describe('GET /api/v1/auth/me', () => {
    it('returns the authenticated user profile when token is provided', async () => {
      const token = signAuthToken({
        id: 'usr-123',
        email: 'listener@vibe.music',
        role: 'USER',
        name: 'Test Listener',
      });

      (prisma.user.findUnique as any).mockResolvedValue({
        id: 'usr-123',
        email: 'listener@vibe.music',
        name: 'Test Listener',
        role: 'USER',
        createdAt: new Date(),
        updatedAt: new Date(),
        _count: { playlists: 3, favourites: 12 },
      });

      const res = await request(app)
        .get('/api/v1/auth/me')
        .set('Authorization', `Bearer ${token}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.user.email).toBe('listener@vibe.music');
      expect(res.body.data.user.playlistCount).toBe(3);
    });

    it('rejects request without authorization header', async () => {
      const res = await request(app).get('/api/v1/auth/me');
      expect(res.status).toBe(401);
    });
  });
});
