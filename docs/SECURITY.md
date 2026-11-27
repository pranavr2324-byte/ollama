# VIBE — Security & Authorization Architecture

---

## 🔒 1. Secrets Management

- **Cloudinary API Credentials**: `CLOUDINARY_API_KEY` and `CLOUDINARY_API_SECRET` reside strictly in the backend environment (`.env`). They are **never** bundled into the Android APK, `local.properties`, or client responses.
- **Supabase Service Keys**: `SUPABASE_SERVICE_ROLE_KEY` is kept server-side for admin bypass; the client only uses JWT tokens.
- **Environment Isolation**: `.env` and `.env.local` files are ignored in `.gitignore`.

---

## 🛡️ 2. Authentication & Authorization

- **JWT Tokens**: Signed using HS256 with `JWT_SECRET` (minimum 32 characters) with a 7-day expiration.
- **Role-Based Access Control (RBAC)**:
  - `USER`: Can read public songs, albums, artists, search, manage own playlists, add/remove favourites, and record playback.
  - `ADMIN`: Can upload songs (`POST /api/v1/admin/songs/upload`), patch song metadata, delete songs, and publish/unpublish tracks.
- **Resource Ownership Enforcement**:
  - Playlist mutation (`PATCH /playlists/:id`, `DELETE /playlists/:id`, `POST /playlists/:id/songs`, `DELETE /playlists/:id/songs/:songId`) strictly verifies that `playlist.userId === req.user.id` unless the requester is an `ADMIN`.
  - User IDs are derived directly from the verified JWT / Supabase session token, never trusted from client request bodies.

---

## 🛡️ 3. Input Validation & Injection Prevention

- **Zod Request Validation**: Every API endpoint validates incoming query parameters and JSON payloads against strict schemas.
- **Safe Cloudinary Public IDs**: Cloudinary asset identifiers are verified against `/^[a-zA-Z0-9_\-/]+$/` before URL generation to prevent SSRF and path traversal.
- **File Upload Security**:
  - `multer` disk storage writes to a temporary directory with unique filenames.
  - File size limit: 50 MB.
  - MIME type whitelist: `audio/mpeg`, `audio/mp3`, `audio/wav`, `audio/x-wav`, `audio/aac`, `audio/flac`, `audio/ogg`, `audio/mp4`, `audio/x-m4a`.
  - Automatic cleanup of temporary files in `finally` blocks.
  - Cloudinary asset cleanup if database persistence fails.

---

## 🌐 4. Network Security

- **Helmet**: Enables HTTP security headers (X-DNS-Prefetch-Control, Frameguard, Hide-Powered-By, HSTS, IEFrames, No-Sniff, XSS-Filter).
- **CORS**: Configurable via `CORS_ORIGIN`.
- **Rate Limiting**: Non-streaming API endpoints are protected by `express-rate-limit` (300 requests per minute).
