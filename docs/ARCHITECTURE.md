# VIBE — Architecture Guide

---

## 🏛️ End-to-End System Architecture

VIBE is a decoupled cloud music streaming platform consisting of a native Android client frontend, a Node.js/TypeScript backend API, Supabase PostgreSQL, and Cloudinary audio storage:

```
┌────────────────────────────────────────────────────────┐
│               VIBE Android Frontend                    │
│  - Jetpack Compose Screens (Home, Explore, etc.)       │
│  - MVVM ViewModels with StateFlow                      │
│  - ExoMusicPlaybackManager (AndroidX Media3 ExoPlayer) │
│  - Remote Repositories with Resilient Mock Fallbacks   │
└──────────────────────────▲─────────────────────────────┘
                           │ HTTPS REST / CDN Audio Stream
                           ▼
┌────────────────────────────────────────────────────────┐
│               VIBE Node.js / Express API               │
│  - Auth Service (JWT + Supabase Auth)                  │
│  - Catalog & Search Controller                         │
│  - Library Controller (Playlists, Favourites, Recent)  │
│  - Admin Upload Controller (Multer Multipart Stream)   │
│  - Cloudinary Streaming Delivery Service               │
└───────────────▲────────────────────────▲───────────────┘
                │                        │
       Prisma Database Queries   Asset Uploads & Signed URLs
                │                        │
                ▼                        ▼
┌────────────────────────┐      ┌────────────────────────┐
│  Supabase PostgreSQL   │      │ Cloudinary Audio CDN   │
│  - Users & Roles       │      │  - vibe/music/ Folder  │
│  - Artists & Albums    │      │  - MP3/WAV/FLAC/AAC    │
│  - Songs & Public IDs  │      │  - HTTP Range Delivery │
│  - Playlists & Songs   │      │  - Fast CDN Streaming  │
│  - Favourites & Recent │      └────────────────────────┘
└────────────────────────┘
```

---

## 🧩 Android Client Layering

1. **UI Layer**:
   - 100% Jetpack Compose and Material 3.
   - Glassmorphism design tokens, animated equalizer bars, responsive sliders.
   - Unidirectional data flow: ViewModels emit immutable `UiState`, UI sends user events.

2. **Repository Layer**:
   - `SongRepository`, `AlbumRepository`, `ArtistRepository`, `PlaylistRepository`.
   - `RemoteSongRepository` etc. query `VibeApiClient` with automatic fallback to bundled mock stores.
   - Runtime configuration via `VibeNetworkConfig` (configurable emulator/device IP and offline toggle).

3. **Audio Engine**:
   - `ExoMusicPlaybackManager` encapsulating `ExoPlayer`.
   - Plays direct Cloudinary streaming URLs (`https://res.cloudinary.com/...`) and bundled raw WAV files.
   - Continuous progress tracking, interactive scrubbing, repeat/shuffle, and queue management.

---

## ☁️ Backend Architecture

1. **Authentication & Authorization**:
   - Dual-mode JWT + Supabase Auth in `authMiddleware.ts`.
   - RBAC protecting administrative upload and mutation endpoints.
   - Ownership verification for all playlist and library mutations.

2. **Storage Architecture (Cloudinary)**:
   - Audio files stored under `vibe/music/`.
   - Uses `video` resource type to enable HTTP `Range` streaming and seek support.
   - Multer disk-buffered streaming upload with automatic temp file cleanup.
   - Orphaned cloud asset deletion if database insertion fails.

3. **Database Layer (Supabase PostgreSQL / Prisma)**:
   - Strictly typed Prisma client (`schema.prisma`).
   - Cascade deletes for relational consistency.
   - Ordered positions for playlist tracks (`playlist_songs`).
