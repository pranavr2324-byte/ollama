import { PrismaClient } from '@prisma/client';
import { env } from './env';

declare global {
  // eslint-disable-next-line no-var
  var __vibePrisma: PrismaClient | undefined;
}

export const prisma: PrismaClient =
  globalThis.__vibePrisma ??
  new PrismaClient({
    log: env.NODE_ENV === 'development' ? ['warn', 'error'] : ['error'],
  });

if (env.NODE_ENV !== 'production') {
  globalThis.__vibePrisma = prisma;
}
