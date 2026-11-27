# VIBE — Product Roadmap

---

## 🗺️ Development Phases

### Phase 1: Native Android Foundation & Design System (Current)
- [x] Environment configuration (Java 21, Android SDK 34, Gradle)
- [x] Initial documentation and technical decisions
- [x] Gradle Kotlin DSL build setup (`com.vibe.music`)
- [ ] VIBE Design System (dark charcoal tokens, glassmorphism, typography, gradients)
- [ ] Media3 ExoPlayer engine integration with sample audio

### Phase 2: Domain Layer & Mock Repositories
- [ ] Define models (`Song`, `Album`, `Artist`, `Playlist`, `Genre`, `User`)
- [ ] Repository interfaces and comprehensive mock datasets:
  - Featured & popular tracks
  - Trending Tamil songs section
  - Recommended albums & artists
  - Mood playlists (Chill, Workout, Focus, Late Night)
- [ ] Reactive in-memory state for user playlists & favourites

### Phase 3: Core UI Screens & Navigation
- [ ] Bottom navigation shell (Home, Explore, Search, Library)
- [ ] Home Screen with dynamic greetings, horizontal carousels, and quick plays
- [ ] Explore Screen with genre tiles and curated picks
- [ ] Reactive Search Screen with filter chips and search history
- [ ] Music Library Screen with tabbed navigation
- [ ] Album, Artist, and Playlist detail screens

### Phase 4: Full-Screen Player & Mini Player
- [ ] Persistent bottom Mini Player with playback controls and tap-to-expand
- [ ] Full-Screen Player with glowing/rotating artwork, seek bar, time display, shuffle, repeat, favourite
- [ ] Real-time Queue management modal (reorder, remove, clear, skip)
- [ ] Settings Screen with audio quality toggles and theme settings

### Phase 5: Verification & APK Assembly
- [ ] Verify complete user flow and playback interactions
- [ ] Assemble debug APK (`./gradlew assembleDebug`)
- [ ] Record exact APK output path and update all documentation

### Phase 6: Future Backend Integration (Post-Frontend)
- [ ] Implement `SongRepository`, `AlbumRepository`, etc. using Retrofit or Ktor
- [ ] Media3 `MediaSessionService` foreground service for background notification controls
- [ ] Cloud audio streaming CDN and caching
- [ ] User authentication (OAuth2 / Firebase Auth)
- [ ] Offline caching and downloads storage
