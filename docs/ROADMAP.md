# VIBE — Product Roadmap

---

## 🗺️ Development Phases

### Phase 1: Native Android Foundation & Design System (Completed)
- [x] Environment configuration (Java 21, Android SDK 34, Gradle)
- [x] Initial documentation and technical decisions
- [x] Gradle Kotlin DSL build setup (`com.vibe.music`)
- [x] VIBE Design System (dark charcoal tokens, glassmorphism, typography, gradients)
- [x] Media3 ExoPlayer engine integration with sample audio

### Phase 2: Domain Layer & Mock Repositories (Completed)
- [x] Define models (`Song`, `Album`, `Artist`, `Playlist`, `Genre`, `PlaybackState`)
- [x] Repository interfaces and comprehensive mock datasets:
  - Featured & popular tracks
  - Trending Tamil songs section
  - Recommended albums & artists
  - Mood playlists (Chill, Workout, Focus, Late Night)
- [x] Reactive in-memory state for user playlists & favourites

### Phase 3: Core UI Screens & Navigation (Completed)
- [x] Bottom navigation shell (Home, Explore, Search, Library)
- [x] Home Screen with dynamic greetings, horizontal carousels, and quick plays
- [x] Explore Screen with genre tiles and curated picks
- [x] Reactive Search Screen with filter chips and search history
- [x] Music Library Screen with tabbed navigation
- [x] Album, Artist, and Playlist detail screens

### Phase 4: Full-Screen Player & Mini Player (Completed)
- [x] Persistent bottom Mini Player with playback controls and tap-to-expand
- [x] Full-Screen Player with glowing/rotating artwork, seek bar, time display, shuffle, repeat, favourite
- [x] Real-time Queue management modal (reorder, remove, clear, skip)
- [x] Settings Screen with audio quality toggles and theme settings

### Phase 5: Verification & APK Assembly (Completed)
- [x] Verify complete user flow and playback interactions
- [x] Assemble debug APK (`./gradlew assembleDebug`)
- [x] Record exact APK output path and update all documentation

### Phase 6: Cloudinary Music Storage Backend & Android Integration (Completed)
- [x] Node.js/TypeScript backend architecture in `backend/`
- [x] Supabase PostgreSQL relational schema via Prisma (`schema.prisma`)
- [x] Cloudinary audio storage integration (`vibe/music/`, HTTP range delivery, signed URLs, deletion)
- [x] Secure multipart audio upload with Zod validation and orphan rollback
- [x] REST APIs for Catalog, Streaming, Playlists, Favourites, Recently Played, and Admin
- [x] Dual JWT & Supabase Authentication with RBAC and resource ownership
- [x] Vitest automated test suite: 22 tests passing across 6 test suites
- [x] Android remote data layer (`VibeApiClient`, `VibeNetworkConfig`, `RemoteSongRepository`, etc.) with dual-mode offline resilience
- [x] Media3 ExoPlayer Cloudinary streaming integration
- [x] Build and verify Android debug APK (`app/build/outputs/apk/debug/app-debug.apk`)
- [x] Comprehensive documentation suite across all 18 required markdown files

### Phase 7: Future Polish & Enhancements (Planned)
- [ ] Implement Media3 `MediaSessionService` foreground notification service for Android lock screen controls.
- [ ] Add Android Room database for local caching of streamed songs and playlists.
- [ ] In-app authentication dialog in Settings tab for Supabase/JWT login.
