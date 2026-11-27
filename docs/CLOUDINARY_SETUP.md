# VIBE — Cloudinary Music Storage Setup

---

## ☁️ Overview

VIBE uses **Cloudinary** as its cloud object storage and HTTP range-streaming delivery provider for audio files.

- **Folder**: `vibe/music/` (configurable via `CLOUDINARY_MUSIC_FOLDER`)
- **Resource Type**: `video` (Cloudinary's standard resource type for audio files, supporting MP3, M4A, AAC, WAV, FLAC, OGG, and HTTP `Range` requests for seeking in Media3 ExoPlayer)
- **Original Audio Preservation**: Files are streamed as-is without forced downsampling or transcoding.

---

## 🔑 Credentials Configuration

In `backend/.env`:

```env
CLOUDINARY_CLOUD_NAME="your-cloud-name"
CLOUDINARY_API_KEY="your-api-key"
CLOUDINARY_API_SECRET="your-api-secret"
CLOUDINARY_MUSIC_FOLDER="vibe/music"
CLOUDINARY_SIGNED_URLS="false"
```

### Security Rules
- Never place `CLOUDINARY_API_KEY` or `CLOUDINARY_API_SECRET` in Android source code or `local.properties`.
- Never commit `.env` to Git.
- Only authenticated `ADMIN` users can upload, update, publish, or delete audio assets.

---

## 📤 Upload Workflow (`POST /api/v1/admin/songs/upload`)

1. Authenticate administrator JWT / Supabase session (`requireAuth` + `requireAdmin`).
2. `multer` writes the multipart `audio` file to a temporary disk path (max 50 MB) and validates MIME types & extensions (`.mp3`, `.m4a`, `.aac`, `.wav`, `.flac`, `.ogg`).
3. Zod validates metadata (`title`, `artistId`/`artistName`, `albumId`/`albumTitle`, `genre`, `artworkUrl`, `releaseDate`, `isPublished`).
4. Duplicate detection checks PostgreSQL by `(title, artistId)` and `(originalFilename, fileSizeBytes)`.
5. `CloudinaryService.uploadAudioFromPath()` streams the file from disk to Cloudinary folder `vibe/music/`.
6. Song metadata and `cloudinaryPublicId` are saved in PostgreSQL via Prisma.
7. If database insertion fails, `CloudinaryService.deleteAudioAsset()` deletes the orphaned cloud asset.
8. The temporary disk file is always deleted in `finally`.

---

## 🎧 Streaming Workflow (`GET /api/v1/songs/:id/stream`)

1. Looks up the published song in PostgreSQL.
2. Generates a secure HTTPS Cloudinary delivery URL from `cloudinaryPublicId` (signed if `CLOUDINARY_SIGNED_URLS=true`).
3. Returns `{ streamUrl, format, durationSeconds }` (or `302` redirect when `?redirect=true` is passed).
4. Android Media3 ExoPlayer streams directly from Cloudinary CDN with full HTTP `Range` seek support.
