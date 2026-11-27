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
