# VIBE — Development Tasks

---

## ✅ Completed Tasks

### Frontend Architecture & Audio Player
- [x] Complete native Android Studio project with Jetpack Compose + Material 3.
- [x] Audio engine wrapping AndroidX Media3 ExoPlayer with queue management, seek bar progress tracking, shuffle, repeat, and volume control.
- [x] 11 Compose screens & interactive components (Home, Explore, Search, Library, Album, Artist, Playlist, Full Player, Mini Player, Live Queue, Settings).
- [x] 5 synthesized royalty-free stereo WAV tracks bundled into `res/raw/` for offline fallback.
- [x] Added `android:usesCleartextTraffic="true"` to `AndroidManifest.xml` for local LAN / emulator API testing.
- [x] Implemented `onPlayerError` listener in `ExoMusicPlaybackManager.kt` to prevent infinite buffering on playback errors.
- [x] Added Android unit test suite (`SongModelTest.kt`) passing via `./gradlew testDebugUnitTest`.
- [x] Assembled and verified fresh debug APK (`app/build/outputs/apk/debug/app-debug.apk`, 32 MB).

### Backend & Cloud Architecture
- [x] Node.js + TypeScript + Express backend structure in `backend/`.
- [x] Cloudinary audio storage service (`CloudinaryService.ts`) with HTTP range-streaming and asset cleanup.
- [x] Production Supabase PostgreSQL schema with Prisma ORM (`prisma/schema.prisma`).
- [x] Secure multipart music upload endpoint (`POST /api/v1/admin/songs/upload`) with rollback protection.
- [x] Complete REST API catalog, streaming, search, playlists, favourites, recently played, and admin endpoints.
- [x] Dual JWT & Supabase Authentication with RBAC and resource ownership checks.
- [x] Administrator onboarding CLI script (`npm run create-admin <email> <password>`).
- [x] Vitest automated test suite expanded to **39 tests passing across 6 test suites**.

### Android Backend Integration
- [x] Configurable network module (`VibeNetworkConfig.kt`) supporting emulator, local LAN, and production base URLs.
- [x] OkHttp client (`VibeApiClient.kt`) with timeouts, auth header interceptor, and sanitized JSON parsers.
- [x] Added `getRecentlyPlayed()` and `getPlaylistById(id)` to `VibeApiClient.kt` and wired into remote repositories.
- [x] Remote repositories (`RemoteSongRepository`, `RemoteAlbumRepository`, `RemoteArtistRepository`, `RemotePlaylistRepository`) with resilient offline mock fallbacks.
- [x] Integrated ExoPlayer direct streaming from Cloudinary URLs (`song.streamUrl != null`).

---

## 🟡 Real-World Cloud Transition Checklist (In Progress)

- [x] Inspect existing repository state, tests, and documentation.
- [x] Protect `backend/.env` with `.gitignore` and generate cryptographically secure `JWT_SECRET`.
- [x] Prepare `backend/.env` template with configuration guides.
- [x] Add zero-leak environment validation script (`npm run check-env`).
- [x] User provides Cloudinary account credentials (`CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`).
- [x] User provides Supabase project credentials (`DATABASE_URL`, `SUPABASE_URL`, `SUPABASE_ANON_KEY`, `SUPABASE_SERVICE_ROLE_KEY`).
- [x] Verify environment variables without exposing secrets (`npm run check-env` & `cloudinary.api.ping()`).
- [x] Update `DATABASE_URL` to Supabase Session Pooler (IPv4) and verify DB connection (`SELECT 1` succeeded, 0 existing tables in `public`).
- [x] Obtain user approval and execute `npx prisma db push` (8 tables created in Supabase `public` schema).
- [x] Run `npm run create-admin` to create `admin@vibe.music` (`ADMIN` role) with a unique generated password.
- [x] Start backend server (`npm run dev`) and verify live endpoints (`/health` & `/api/v1/songs`).
- [x] Upload first real song (`Putaapu Gang`, 6.2 MB MP3) via `POST /api/v1/admin/songs/upload`.
- [x] Verify song in Cloudinary (`vibe/music/Putaapu-Gang-Song-SnapYT.App_1_mvlvid`) and Supabase database (`songs` & `artists`).
- [x] Test HTTP range streaming URL (`206 Partial Content`, `audio/mpeg`, `Accept-Ranges: bytes`).
- [x] Assemble fresh debug APK (`app/build/outputs/apk/debug/app-debug.apk`, 32 MB) and verify live playback of **Putaapu Gang** on user's physical Android phone.
- [x] Deploy backend to Render production (`https://vibe-backend-qeil.onrender.com`) — health check and song API verified.
- [x] Update `VibeNetworkConfig.kt` `DEFAULT_PUBLIC_URL` to production Render URL.
- [x] Rebuild debug APK pointing to production backend.

---

## 🔍 E2E QA & Release Validation (2026-11-27)

- [x] Verified live Render API (`/health`, `/songs`, `/songs/:id`, `/songs/:id/stream`, `/albums`, `/artists`, `/search`), Supabase PostgreSQL, and Cloudinary HTTP `206 Partial Content` Range streaming.
- [x] Fixed `GET /api/v1/playlists/:id` ownership/auth leak (`libraryRoutes.ts`, `libraryController.ts`) + added 3 regression tests (`42/42` Vitest tests passing).
- [x] Fixed `POST /api/v1/auth/register` Supabase email rate-limit blocker (`authController.ts` using `admin.createUser`).
- [x] Added Media3 `AudioAttributes` (`handleAudioFocus = true`) and `PlaybackState.errorMessage` banner (`ExoMusicPlaybackManager.kt`, `PlaybackState.kt`, `PlayerScreen.kt`).
- [x] Verified `testDebugUnitTest`, `lintDebug`, and `assembleDebug` passing (`app/build/outputs/apk/debug/app-debug.apk`, 32 MB).
- [ ] Deploy updated backend fixes (`errorHandler.ts`, `libraryRoutes.ts`, `libraryController.ts`, `authController.ts`) to Render.
- [ ] Implement Android Login/Register/Logout UI + `VibeApiClient` auth methods so user playlists/favourites/recently-played sync to cloud.
- [ ] Implement `MediaSessionService` in `AndroidManifest.xml` for lock-screen, notification controls, and background playback.
- [ ] Configure release signing and restrict `usesCleartextTraffic` to debug builds only before public release.
- [ ] Run physical device UI, rotation, backgrounding, and Logcat tests on user's Android phone (`adb devices` had no attached device in Codespace).
