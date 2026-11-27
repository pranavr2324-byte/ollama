import bcrypt from 'bcryptjs';
import { Request, Response } from 'express';
import { prisma } from '../config/prisma';
import { getSupabaseAdminClient, getSupabaseClient } from '../config/supabase';
import { signAuthToken } from '../middleware/authMiddleware';
import { AppError } from '../utils/AppError';
import { loginSchema, registerSchema } from '../validators/authValidator';

export async function register(req: Request, res: Response): Promise<void> {
  const { email, password, name } = registerSchema.parse(req.body);
  const normalizedEmail = email.toLowerCase();

  const existing = await prisma.user.findUnique({ where: { email: normalizedEmail } });
  if (existing) {
    throw new AppError('An account with this email already exists', 409, 'EMAIL_EXISTS');
  }

  let supabaseId: string | null = null;
  const supabaseAdmin = getSupabaseAdminClient();
  if (supabaseAdmin) {
    const { data, error } = await supabaseAdmin.auth.admin.createUser({
      email: normalizedEmail,
      password,
      email_confirm: true,
      user_metadata: { name },
    });
    if (error) {
      throw new AppError(error.message, 400, 'SUPABASE_REGISTER_FAILED');
    }
    supabaseId = data.user?.id ?? null;
  } else {
    const supabase = getSupabaseClient();
    if (supabase) {
      const { data, error } = await supabase.auth.signUp({
        email: normalizedEmail,
        password,
        options: { data: { name } },
      });
      if (error) {
        throw new AppError(error.message, 400, 'SUPABASE_REGISTER_FAILED');
      }
      supabaseId = data.user?.id ?? null;
    }
  }

  const passwordHash = await bcrypt.hash(password, 10);
  const user = await prisma.user.create({
    data: {
      email: normalizedEmail,
      name: name ?? null,
      passwordHash,
      supabaseId,
      role: 'USER',
    },
    select: { id: true, email: true, name: true, role: true },
  });

  const token = signAuthToken({
    id: user.id,
    email: user.email,
    role: user.role,
    name: user.name,
  });

  res.status(201).json({
    success: true,
    data: { token, user },
  });
}

export async function login(req: Request, res: Response): Promise<void> {
  const { email, password } = loginSchema.parse(req.body);
  const normalizedEmail = email.toLowerCase();

  const user = await prisma.user.findUnique({
    where: { email: normalizedEmail },
  });

  if (!user || !user.passwordHash) {
    const supabase = getSupabaseClient();
    if (supabase) {
      const { data, error } = await supabase.auth.signInWithPassword({
        email: normalizedEmail,
        password,
      });
      if (!error && data.user && data.session) {
        const syncedUser =
          user ??
          (await prisma.user.create({
            data: {
              email: normalizedEmail,
              supabaseId: data.user.id,
              name: (data.user.user_metadata?.name as string | undefined) ?? null,
              role: 'USER',
            },
          }));
        res.json({
          success: true,
          data: {
            token: data.session.access_token,
            user: {
              id: syncedUser.id,
              email: syncedUser.email,
              name: syncedUser.name,
              role: syncedUser.role,
            },
          },
        });
        return;
      }
    }
    throw new AppError('Invalid email or password', 401, 'INVALID_CREDENTIALS');
  }

  const isValid = await bcrypt.compare(password, user.passwordHash);
  if (!isValid) {
    throw new AppError('Invalid email or password', 401, 'INVALID_CREDENTIALS');
  }

  const token = signAuthToken({
    id: user.id,
    email: user.email,
    role: user.role,
    name: user.name,
  });

  res.json({
    success: true,
    data: {
      token,
      user: {
        id: user.id,
        email: user.email,
        name: user.name,
        role: user.role,
      },
    },
  });
}

export async function logout(_req: Request, res: Response): Promise<void> {
  res.json({
    success: true,
    message: 'Logged out successfully',
  });
}

export async function getMe(req: Request, res: Response): Promise<void> {
  const userId = req.user?.id;
  if (!userId) {
    throw new AppError('Authentication required', 401, 'UNAUTHORIZED');
  }

  const user = await prisma.user.findUnique({
    where: { id: userId },
    select: {
      id: true,
      email: true,
      name: true,
      role: true,
      createdAt: true,
      _count: { select: { playlists: true, favourites: true } },
    },
  });

  if (!user) {
    res.json({
      success: true,
      data: { user: req.user },
    });
    return;
  }

  res.json({
    success: true,
    data: {
      user: {
        id: user.id,
        email: user.email,
        name: user.name,
        role: user.role,
        playlistCount: user._count.playlists,
        favouriteCount: user._count.favourites,
      },
    },
  });
}
