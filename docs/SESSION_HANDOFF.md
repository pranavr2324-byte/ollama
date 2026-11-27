# VIBE — Session Handoff Document

---

## 🤝 Overview for Incoming AI Agents & Engineers

This document provides a complete briefing so that any AI coding platform (Antigravity, Claude Code, Codex, Alibaba Cloud, Qwen) or human Android engineer can immediately inspect, run, test, and continue development on **VIBE**.

---

## 🎯 What Has Been Built

1. **Complete Native Android App Structure**:
   - Application ID: `com.vibe.music`
   - Target SDK: 34 (Android 14)
   - Min SDK: 26 (Android 8.0)
   - 100% Kotlin + Jetpack Compose + Material 3 + Media3 ExoPlayer.
   - Zero web, HTML, React, or non-native code.

2. **Design System**:
   - Bespoke dark nocturnal theme (`#0A0A0E` background, `#14141B` surface).
   - Electric Violet (`#8B5CF6`) and Neon Cyan (`#06B6D4`) primary gradients.
   - Glassmorphic card surfaces (`GlassCard`), animated equalizer bars (`AnimatedEqualizer`), gradient CTA buttons (`VibeGradientButton`), custom vector launcher icons.

3. **Audio Engine (`MusicPlaybackManager` / `ExoMusicPlaybackManager`)**:
   - Encapsulates AndroidX Media3 ExoPlayer.
   - Fully interactive playback state flow (`PlaybackState`).
   - Supports play, pause, toggle, skip next, skip previous, seek to position/fraction, repeat modes (OFF, ALL, ONE), shuffle toggle, volume slider, queue management (reorder, remove, clear, direct skip).
   - Bundles 5 royalty-free stereo WAV audio files in `app/src/main/res/raw/` for complete offline playback.

4. **11 Complete Screens & Components**:
   - **Home Screen**: Greeting, VIBE branding, Hero featured card, Trending Tamil songs, Recently Played carousel, Recommended Albums, Popular Artists, Mood Playlists.
   - **Explore Screen**: Quick search trigger, category chips, New Releases carousel, Browse All Genres 8-tile gradient grid, Trending Tracks, Curated Playlists.
   - **Search Screen**: Reactive debounced search across songs/albums/artists/playlists, category filter chips, search history chips with delete/clear all, empty & loading states.
   - **Music Library Screen**: 5 Tabbed views (Songs, Albums, Artists, Playlists, Favourites), Create Playlist modal dialog with real-time in-memory persistence.
   - **Album Detail Screen**: Artwork header, title, artist, year, track count, Play All, Shuffle, song list.
   - **Artist Detail Screen**: Large circular portrait with neon rim, listener stats, bio, Play Popular, Shuffle, popular tracks.
   - **Playlist Detail Screen**: Artwork header, description, song count, Play All, Shuffle, song list.
   - **Full-Screen Music Player**: Vinyl spin animation, ambient glow, title/artist, favorite toggle, interactive scrubbable seek slider with elapsed/remaining times, shuffle, previous, play/pause, next, repeat mode cycling, interactive volume slider, queue sheet trigger.
   - **Persistent Mini Player**: Bottom anchored across all main screens, thumbnail, title, artist, play/pause, next, micro progress bar, tap to expand into full player.
   - **Live Playback Queue**: Modal bottom sheet, current track with animated equalizer, reorder items up/down, remove item, clear queue, tap to skip.
   - **Settings Screen**: Audio streaming quality selector, download quality, theme switcher, gapless playback toggle, volume normalization, app info, user profile card.

5. **Decoupled Architecture**:
   - All ViewModels depend exclusively on repository interfaces (`SongRepository`, `AlbumRepository`, `ArtistRepository`, `PlaylistRepository`) and `MusicPlaybackManager`.
   - Mock repositories (`MockSongRepository`, `MockAlbumRepository`, etc.) simulate network operations and in-memory updates.

---

## ⚡ How to Build and Run the Project

### Environment Variables
```bash
export ANDROID_HOME=/home/codespace/android-sdk
export JAVA_HOME=/usr/local/sdkman/candidates/java/21.0.13.0+2-ms
```

### Build Command
```bash
./gradlew assembleDebug
```

### Verified APK Location
```
app/build/outputs/apk/debug/app-debug.apk
```
File size: **~32 MB**

---

## 📱 How to Test on Device or Emulator

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.vibe.music.debug/com.vibe.music.MainActivity
```

---

## 🔄 Exact Next Task for Continuing Development

When integrating a cloud backend:
1. Create a `com.vibe.music.data.remote` package with Retrofit or Ktor HTTP client.
2. Implement `SongRepository`, `AlbumRepository`, `ArtistRepository`, and `PlaylistRepository` to query backend REST/GraphQL endpoints.
3. Replace the mock repository instantiations in `VibeApplication.kt` with the remote implementations. No UI or ViewModel changes are required!
4. Implement a `MediaSessionService` foreground service in `com.vibe.music.player` to enable Android system lock-screen and notification bar media controls.
