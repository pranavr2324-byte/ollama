import { NextFunction, Request, Response } from 'express';
import jwt from 'jsonwebtoken';
import { env } from '../config/env';
import { prisma } from '../config/prisma';
import { getSupabaseClient } from '../config/supabase';
import { AppError } from '../utils/AppError';

export interface AuthenticatedUser {
  id: string;
  email: string;
  role: 'USER' | 'ADMIN';
  name?: string | null;
}

declare global {
  namespace Express {
    interface Request {
      user?: AuthenticatedUser;
    }
  }
}

interface JwtPayload {
  sub: string;
  email: string;
  role: 'USER' | 'ADMIN';
}

export function signAuthToken(user: AuthenticatedUser): string {
  return jwt.sign(
    { sub: user.id, email: user.email, role: user.role },
    env.JWT_SECRET,
    { expiresIn: '7d' }
  );
}

async function resolveUserFromToken(token: string): Promise<AuthenticatedUser | null> {
  // 1. Try local JWT verification
  try {
    const decoded = jwt.verify(token, env.JWT_SECRET) as JwtPayload;
    if (decoded?.sub) {
      try {
        const dbUser = await prisma.user.findUnique({
          where: { id: decoded.sub },
          select: { id: true, email: true, role: true, name: true },
        });
        if (dbUser) {
          return {
            id: dbUser.id,
            email: dbUser.email,
            role: dbUser.role,
            name: dbUser.name,
          };
        }
      } catch {
        // DB lookup failure or offline
      }
      return {
        id: decoded.sub,
        email: decoded.email,
        role: decoded.role,
      };
    }
  } catch {
    // Fall through to Supabase token verification if configured
  }

  // 2. Try Supabase Auth JWT verification
  const supabase = getSupabaseClient();
  if (supabase) {
    const { data, error } = await supabase.auth.getUser(token);
    if (!error && data.user?.email) {
      let dbUser = await prisma.user.findFirst({
        where: {
          OR: [{ supabaseId: data.user.id }, { email: data.user.email }],
        },
        select: { id: true, email: true, role: true, name: true },
      });

      if (!dbUser) {
        dbUser = await prisma.user.create({
          data: {
            email: data.user.email,
            supabaseId: data.user.id,
            name: (data.user.user_metadata?.name as string | undefined) ?? null,
            role: 'USER',
          },
          select: { id: true, email: true, role: true, name: true },
        });
      }

      return {
        id: dbUser.id,
        email: dbUser.email,
        role: dbUser.role,
        name: dbUser.name,
      };
    }
  }

  return null;
}

export async function requireAuth(
  req: Request,
  _res: Response,
  next: NextFunction
): Promise<void> {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    next(new AppError('Authentication token is required', 401, 'UNAUTHORIZED'));
    return;
  }

  const token = authHeader.slice('Bearer '.length).trim();
  if (!token) {
    next(new AppError('Authentication token is empty', 401, 'UNAUTHORIZED'));
    return;
  }

  try {
    const user = await resolveUserFromToken(token);
    if (!user) {
      next(new AppError('Invalid or expired authentication token', 401, 'INVALID_TOKEN'));
      return;
    }
    req.user = user;
    next();
  } catch (err) {
    next(err);
  }
}

export async function optionalAuth(
  req: Request,
  _res: Response,
  next: NextFunction
): Promise<void> {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    next();
    return;
  }

  const token = authHeader.slice('Bearer '.length).trim();
  if (!token) {
    next();
    return;
  }

  try {
    const user = await resolveUserFromToken(token);
    if (user) {
      req.user = user;
    }
  } catch {
    // Optional auth ignores invalid tokens
  }
  next();
}

export function requireAdmin(
  req: Request,
  _res: Response,
  next: NextFunction
): void {
  if (!req.user) {
    next(new AppError('Authentication required', 401, 'UNAUTHORIZED'));
    return;
  }
  if (req.user.role !== 'ADMIN') {
    next(new AppError('Administrator privileges required', 403, 'ADMIN_ACCESS_REQUIRED'));
    return;
  }
  next();
}
