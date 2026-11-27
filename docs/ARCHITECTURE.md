# VIBE — Architecture Guide

---

## 🏛️ Architecture Overview

VIBE adheres to clean architecture principles with a strict unidirectional data flow (UDF) powered by Jetpack Compose and MVVM.

```
┌────────────────────────────────────────────────────────┐
│                      UI Layer                          │
│  - Jetpack Compose Screens (Home, Player, etc.)        │
│  - Reusable Components (GlassCard, MiniPlayer, etc.)   │
│  - Theme Tokens (Colors, Typography, Gradients)        │
└──────────────────────────▲─────────────────────────────┘
                           │ StateFlow / Events
┌──────────────────────────┴─────────────────────────────┐
│                   ViewModel Layer                      │
│  - HomeViewModel, PlayerViewModel, SearchViewModel...   │
│  - Coroutine Scopes & UI State Management              │
└──────────────────────────▲─────────────────────────────┘
                           │ Repositories / Manager
┌──────────────────────────┴─────────────────────────────┐
│                    Domain & Data                       │
│  - Interfaces: SongRepository, AlbumRepository, etc.   │
│  - Mock Implementations / Future Network Repositories   │
│  - MusicPlaybackManager (Media3 ExoPlayer wrapper)     │
└────────────────────────────────────────────────────────┘
```

---

## 🧩 Key Architectural Principles

1. **Clean Separation of Concerns**:
   - UI has zero direct dependency on network or storage details.
   - All data is retrieved through repository interfaces (`SongRepository`, `AlbumRepository`, `ArtistRepository`, `PlaylistRepository`).
   - Playback is encapsulated entirely behind `MusicPlaybackManager`.

2. **Unidirectional Data Flow (UDF)**:
   - ViewModels expose immutable `StateFlow<UiState>`.
   - UI triggers event callbacks on the ViewModel (e.g. `onSongClicked`, `onPlayPauseToggle`).
   - State flows down, events flow up.

3. **Backend-Ready Abstraction**:
   - When a cloud backend (REST or GraphQL) is introduced, developers only need to implement the repository interfaces with Retrofit/Ktor.
   - Zero UI changes will be required to transition from local mock data to real streaming APIs.

4. **Media3 ExoPlayer Abstraction**:
   - `MusicPlaybackManager` manages `ExoPlayer` lifecycle, media item queues, repeat modes, shuffle modes, and progress state emissions via `StateFlow<PlayerState>`.
   - Supports both bundled local audio assets (sample tracks) and remote streaming URLs.
