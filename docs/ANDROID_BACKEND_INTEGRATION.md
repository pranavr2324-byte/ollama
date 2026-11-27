# VIBE — Android & Backend Integration Guide

---

## 📱 Architecture Overview

The VIBE Android client communicates with the Cloudinary & Node/Express backend through a decoupled remote repository layer:

```
Jetpack Compose Screens & ViewModels
             │ (StateFlow / UDF)
             ▼
    Repository Interfaces
  (SongRepository, etc.)
             │
             ▼
    Remote Repositories
(RemoteSongRepository, etc.)
      ┌──────┴──────┐
      │             │
(Remote Mode)   (Offline Fallback)
      │             │
      ▼             ▼
 VibeApiClient   Mock Stores
(OkHttp Client) (res/raw audio)
      │
      ▼
 VIBE Backend (Node/Express)
 ├── PostgreSQL (Supabase / Prisma)
 └── Cloudinary CDN (Direct Audio Stream)
```

---

## 🌐 Network Configuration (`VibeNetworkConfig`)

The backend base URL is configurable at runtime via `VibeNetworkConfig`:

| Environment | Base URL |
|---|---|
| **Android Emulator** | `http://10.0.2.2:4000/api/v1/` (default) |
| **Physical Android Device** | `http://<your-lan-ip>:4000/api/v1/` |
| **Production Server** | `https://api.vibe.music/api/v1/` |

### Changing Base URL at Runtime:
```kotlin
VibeNetworkConfig.baseUrl = "http://192.168.1.100:4000/api/v1/"
```

### Dev Mode Toggle:
```kotlin
// Toggle between remote backend and local offline mock
VibeNetworkConfig.isRemoteEnabled = true // or false
```

---

## 🎧 Media3 ExoPlayer Audio Streaming

`ExoMusicPlaybackManager` automatically plays audio from Cloudinary HTTPS delivery URLs returned by the backend:

1. When a song with `streamUrl != null` is selected, `ExoMusicPlaybackManager` creates a `MediaItem`:
   ```kotlin
   val uri = when {
       song.audioResId != null -> Uri.parse("android.resource://${context.packageName}/${song.audioResId}")
       song.streamUrl != null -> Uri.parse(song.streamUrl)
       else -> Uri.EMPTY
   }
   ```
2. ExoPlayer sends HTTP `Range` requests directly to Cloudinary's global CDN.
3. Media3 buffers audio chunks on demand, enabling instantaneous seek, pause, resume, and background playback.
4. If a song does not have a `streamUrl` (e.g. offline sample track), it falls back to bundled raw WAV audio in `res/raw/`.

---

## 🔄 Dual-Mode Offline Resilience

All remote repositories (`RemoteSongRepository`, `RemoteAlbumRepository`, `RemoteArtistRepository`, `RemotePlaylistRepository`) wrap their respective mock implementations.

If the backend server is temporarily unreachable, network requests timeout, or device is offline:
- The repositories gracefully fall back to local mock stores.
- The UI never crashes or shows broken blank states.
- The user can continue listening to bundled offline music.
