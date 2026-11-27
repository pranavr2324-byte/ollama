# VIBE — Premium Native Android Music Application

<p align="center">
  <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="VIBE Logo" width="120" height="120" onerror="this.style.display='none'"/>
</p>

<p align="center">
  <b>Immersive, elegant, cloud-ready music streaming frontend built with modern Android Jetpack Compose & Media3 ExoPlayer.</b>
</p>

---

## 🎧 Overview

**VIBE** is a modern cloud-ready music streaming Android frontend crafted with:
- **Kotlin** & **Jetpack Compose** (Declarative UI)
- **Material 3** with custom deep charcoal / neon gradient aesthetics
- **AndroidX Media3 ExoPlayer** for seamless local & streaming audio playback
- **MVVM Architecture** with unidirectional data flow (StateFlow & Coroutines)
- **Navigation Compose** with smooth transitions
- **Gradle Kotlin DSL** (`build.gradle.kts`)

VIBE delivers a music-focused experience without social feeds, video bloat, or distracting non-music features.

---

## 📱 Feature Highlights

- **Home Screen**: Personalized greeting, featured tracks, Trending Tamil hits, Recently Played, Recommended Albums, Popular Artists, and Mood Playlists.
- **Explore Screen**: Category badges, trending songs, new releases, genre grids, featured artists, and curated playlists.
- **Search Screen**: Instant reactive search across songs, albums, and artists, search history management, and empty/loading states.
- **Music Library**: Tabs for All Songs, Albums, Artists, Playlists, and Favourites.
- **Album & Artist Screens**: Dynamic header artwork, track lists, quick play, shuffle, and add-to-playlist options.
- **Custom Playlist Management**: Create playlists, edit titles, add/remove songs, and toggle favourites.
- **Persistent Mini Player**: Bottom anchored with album art thumbnail, title, artist, play/pause, next track, and progress bar, tap to expand to full player.
- **Full-Screen Audio Player**: High-res album art with animated vinyl/glow effect, interactive seek bar, playback controls (Play/Pause, Skip Next/Previous, Shuffle, Repeat 1/All, Favourite toggle, Queue quick sheet, Volume slider).
- **Playback Queue**: Live reorderable/clearable queue, upcoming songs, direct track selection.
- **Settings Screen**: Audio streaming & download quality preferences, dark aesthetic themes, equalizer shortcut, app info, and user profile view.

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin 2.0+ |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Media Player** | AndroidX Media3 (ExoPlayer 1.4+) |
| **Architecture** | MVVM + Clean Architecture Repositories |
| **Async / Reactive** | Kotlin Coroutines + StateFlow / SharedFlow |
| **Navigation** | Navigation Compose |
| **Image Loading** | Coil Compose |
| **Build System** | Gradle 8.x / Kotlin DSL (`build.gradle.kts`) |
| **Target SDK** | Android 14 (API 34), Min SDK 26 (Android 8.0) |

---

## 🏗️ Project Architecture

```
com.vibe.music
├── data
│   ├── model          # Song, Album, Artist, Playlist, Genre data classes
│   └── repository     # Clean interfaces & MockRepository implementations
├── player             # MusicPlaybackManager & Media3 ExoPlayer implementation
├── ui
│   ├── components     # Glassmorphic cards, TrackItem, MiniPlayer, GradientHeader
│   ├── navigation     # Screen definitions, NavigationHost, BottomNavBar
│   ├── screens        # Home, Explore, Search, Library, Album, Artist, Playlist, Player, Queue, Settings
│   └── theme          # VibeColors, Typography, Shape, VibeTheme
└── viewmodel          # HomeViewModel, PlayerViewModel, LibraryViewModel, etc.
```

---

## 🚀 Building & Running

### Prerequisites
- JDK 17 or 21 (`JAVA_HOME` pointing to Java 17/21)
- Android SDK (API 34 installed, `ANDROID_HOME` configured)

### Build Debug APK
```bash
export ANDROID_HOME=/home/codespace/android-sdk
export JAVA_HOME=/usr/local/sdkman/candidates/java/21.0.13.0+2-ms
./gradlew assembleDebug
```
The resulting APK is generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📖 Complete Documentation Index

All detailed specifications and engineering documents are located in `/docs`:
- [PROJECT_STATE.md](PROJECT_STATE.md) — Live implementation status and test results
- [TASKS.md](TASKS.md) — Development task checklist
- [PRODUCT_SPEC.md](PRODUCT_SPEC.md) — Complete product specifications & functional details
- [UI_DESIGN_SYSTEM.md](UI_DESIGN_SYSTEM.md) — Design tokens, typography, glassmorphism, and color palette
- [ARCHITECTURE.md](ARCHITECTURE.md) — Architecture diagrams and layer separation
- [DECISIONS.md](DECISIONS.md) — Architecture and design decision log
- [ROADMAP.md](ROADMAP.md) — Roadmap for backend integration and cloud streaming
- [SESSION_HANDOFF.md](SESSION_HANDOFF.md) — Seamless handoff instructions for any AI coding agent
- [AGENTS.md](AGENTS.md) — Guidelines for autonomous AI agents
