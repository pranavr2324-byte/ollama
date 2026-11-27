# VIBE — Technical Decision Records (ADR)

---

## 📋 Architecture Decisions

### ADR-001: Jetpack Compose & Material 3 for UI
- **Context**: The app requires a modern, fluid, premium design system with custom dark aesthetics, smooth animations, and glassmorphic card effects.
- **Decision**: Adopt 100% Jetpack Compose and Material 3 rather than legacy XML Views.
- **Consequences**: Fast declarative UI development, easy state observation with `collectAsState()`, and lightweight custom styling.

### ADR-002: AndroidX Media3 ExoPlayer for Audio Engine
- **Context**: The app needs robust media playback supporting queue management, seek, shuffle, repeat modes, background audio handling, and smooth transitions.
- **Decision**: Use `androidx.media3:media3-exoplayer` and `media3-ui`.
- **Consequences**: Standardized media engine supported directly by Google, ready for MediaSession service expansion, and reliable playback across audio formats.

### ADR-003: Pure Interface Repositories for Mock & Future Backend
- **Context**: The frontend must function locally out of the box while remaining decoupled from cloud APIs.
- **Decision**: Model all domain access through explicit interfaces (`SongRepository`, `AlbumRepository`, `ArtistRepository`, `PlaylistRepository`) implemented with reactive mock stores that simulate real network delays and in-memory persistence.
- **Consequences**: Any backend developer can replace mock classes with HTTP clients without modifying any Jetpack Compose or ViewModel code.

### ADR-004: Royalty-Free Bundled Audio for Local Verification
- **Context**: Copyrighted music cannot be bundled into the repository, but the application must play real audible sound during verification and user testing.
- **Decision**: Bundle high-quality royalty-free audio files into Android raw/asset resources and map mock metadata to local resource URIs.
- **Consequences**: Legal, self-contained, fully offline testable playback.

### ADR-005: Deep Charcoal & Electric Violet/Cyan Design Identity
- **Context**: The user specified an original, premium visual identity (deep black/charcoal backgrounds, minimal interface, subtle glass effects, vibrant gradient accents, avoid generic templates).
- **Decision**: Define a bespoke design system (`VibeTheme`, `VibeColors`) with `#0A0A0E` background, `#14141B` surfaces, `#8B5CF6` (electric violet) and `#06B6D4` (neon cyan) gradient highlights.
- **Consequences**: Visually striking, high contrast, focused on music immersion.

### ADR-006: Gradle Memory Limits and Native Packaging Rules
- **Context**: Linux containers in cloud dev environments (such as Codespaces) run with strict memory bounds without swap. Default Gradle daemon memory settings and D8 dex workers could cause OOM. Furthermore, NDK debug symbol stripping can fail when standalone NDK tools are not present.
- **Decision**: Configure `org.gradle.jvmargs=-Xmx2560m -XX:+UseG1GC` with `org.gradle.workers.max=2`, and set `keepDebugSymbols += "**/*.so"` in `packaging.jniLibs`.
- **Consequences**: Fast, stable builds that consistently produce valid APKs without crashing the Gradle daemon.

### ADR-007: Cloudinary as Cloud Audio Storage & Range-Streaming Provider
- **Context**: Large audio files should not be stored in PostgreSQL or proxy-streamed through the Node.js server memory, which would degrade throughput.
- **Decision**: Use Cloudinary with dedicated `vibe/music/` folder and `video` resource type. Cloudinary provides global CDN delivery, HTTP `Range` request support for seeking in ExoPlayer, and signed delivery URLs when needed.
- **Consequences**: High-speed audio delivery, zero server CPU overhead during streaming, and reliable seeking.

### ADR-008: Supabase PostgreSQL with Prisma ORM for Catalog & Relational State
- **Context**: The backend needs relational integrity for artists, albums, songs, user-created playlists with explicit ordering (`playlist_songs`), favourites, and recently played tracks.
- **Decision**: Use PostgreSQL hosted via Supabase and managed via Prisma ORM (`backend/prisma/schema.prisma`).
- **Consequences**: Type-safe queries, migration management, and foreign key cascade behaviors.

### ADR-009: Hybrid Dual-Auth (JWT + Supabase Auth)
- **Context**: The backend must support both local standalone development (bcrypt + JWT) and cloud Supabase authentication without requiring developers to sign up for external services just to run unit tests.
- **Decision**: Implement dual authentication in `authController.ts` and `authMiddleware.ts` that automatically falls back to local JWT auth when Supabase credentials are absent.
- **Consequences**: 100% testable in CI/CD without live cloud dependencies.

### ADR-010: Android Remote Repository Layer with Resilient Offline Fallback
- **Context**: If network is disconnected or backend is offline, the Android app should not show broken screens or crash.
- **Decision**: Implement `RemoteSongRepository`, `RemoteAlbumRepository`, `RemoteArtistRepository`, and `RemotePlaylistRepository` wrapping `VibeApiClient` with automatic fallback to bundled mock stores.
- **Consequences**: Uncompromised user experience, instant responsiveness, and zero breaking changes to existing ViewModels.
