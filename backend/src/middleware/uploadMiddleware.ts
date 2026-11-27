import fs from 'fs';
import multer from 'multer';
import os from 'os';
import path from 'path';
import { AppError } from '../utils/AppError';

export const MAX_AUDIO_FILE_SIZE_BYTES = 50 * 1024 * 1024; // 50 MB

export const SUPPORTED_AUDIO_MIME_TYPES = new Set([
  'audio/mpeg',
  'audio/mp3',
  'audio/mp4',
  'audio/x-m4a',
  'audio/m4a',
  'audio/aac',
  'audio/x-aac',
  'audio/wav',
  'audio/x-wav',
  'audio/wave',
  'audio/flac',
  'audio/x-flac',
  'audio/ogg',
]);

export const SUPPORTED_AUDIO_EXTENSIONS = new Set([
  '.mp3',
  '.m4a',
  '.aac',
  '.wav',
  '.flac',
  '.ogg',
]);

const uploadDir = path.join(os.tmpdir(), 'vibe-uploads');
if (!fs.existsSync(uploadDir)) {
  fs.mkdirSync(uploadDir, { recursive: true });
}

const storage = multer.diskStorage({
  destination: (_req, _file, cb) => {
    cb(null, uploadDir);
  },
  filename: (_req, file, cb) => {
    const ext = path.extname(file.originalname).toLowerCase();
    const uniqueSuffix = `${Date.now()}-${Math.round(Math.random() * 1e9)}`;
    cb(null, `vibe-audio-${uniqueSuffix}${ext}`);
  },
});

export const audioUploadMiddleware = multer({
  storage,
  limits: {
    fileSize: MAX_AUDIO_FILE_SIZE_BYTES,
    files: 1,
  },
  fileFilter: (_req, file, cb) => {
    const ext = path.extname(file.originalname).toLowerCase();
    const mime = file.mimetype.toLowerCase();

    if (!SUPPORTED_AUDIO_EXTENSIONS.has(ext) && !SUPPORTED_AUDIO_MIME_TYPES.has(mime)) {
      cb(
        new AppError(
          `Unsupported audio format (${mime || ext}). Supported formats: MP3, M4A, AAC, WAV, FLAC, OGG`,
          400,
          'UNSUPPORTED_AUDIO_FORMAT'
        )
      );
      return;
    }
    cb(null, true);
  },
});

export async function cleanupTempFile(filePath?: string): Promise<void> {
  if (!filePath) return;
  try {
    await fs.promises.unlink(filePath);
  } catch {
    // Ignore if already removed
  }
}
