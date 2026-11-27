# VIBE — Premium Native Android Music Application & Cloud Backend

<p align="center">
  <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="VIBE Logo" width="120" height="120" onerror="this.style.display='none'"/>
</p>

<p align="center">
  <b>Immersive, elegant, cloud-ready music streaming platform built with Android Jetpack Compose, Media3 ExoPlayer, Node.js TypeScript API, Cloudinary Music Storage, and Supabase PostgreSQL.</b>
</p>

---

## 🎧 Overview

**VIBE** is a production-grade music streaming application consisting of:
- **Android Native Client**: Kotlin, Jetpack Compose, Material 3, AndroidX Media3 ExoPlayer, MVVM with unidirectional data flow, OkHttp remote repository layer with offline fallback.
- **Backend API**: Node.js, Express, TypeScript, Zod, JWT & Supabase Auth, Prisma ORM, Helmet security, rate limiting.
- **Cloud Storage**: Cloudinary dedicated `vibe/music/` audio storage with HTTP `Range` request delivery for high-performance seeking in Media3 ExoPlayer.
- **Database**: Supabase PostgreSQL with relational schema for artists, albums, songs, ordered playlists, favourites, and recently played tracks.

---

## 📱 Feature Highlights

- **Home Screen**: Personalized greeting, hero featured tracks, Trending Tamil hits, Recently Played, Recommended Albums, Popular Artists, and Mood Playlists.
- **Explore Screen**: Category badges, trending tracks, new releases carousel, 8-tile genre grid, and curated playlists.
- **Search Screen**: Reactive debounced search across songs, albums, and artists, filter chips, and search history chips.
- **Music Library**: Tabs for Songs, Albums, Artists, Playlists, and Favourites, with real-time playlist creation and cloud sync.
- **Full-Screen Audio Player**: High-res album art with animated vinyl effect and ambient glow, scrubbable seek slider with elapsed/remaining times, Play/Pause, Next/Previous, Shuffle, Repeat (Off/All/One), Favourite toggle, Queue trigger, Volume slider.
- **Persistent Mini Player**: Anchored across all destinations with thumbnail, title, artist, playback controls, and micro progress bar.
- **Live Playback Queue**: Modal bottom sheet, current track animated equalizer, reorder items up/down, remove item, clear queue, click to skip.
- **Settings Screen**: Audio streaming & download quality preferences, dark aesthetic themes, volume normalization, and user profile card.
- **Cloudinary Range Streaming**: Seamless seeking and buffering directly through Cloudinary CDN.
- **Dual-Mode Offline Resilience**: Operates in cloud streaming mode when connected, seamlessly falls back to bundled synthesized raw audio when offline.

---

## 🛠️ Technology Stack

| Component | Technology |
|---|---|
| **Android Language & SDK** | Kotlin 2.0+, Target SDK 34 (Android 14), Min SDK 26 |
| **Android UI Framework** | Jetpack Compose + Material 3 + Navigation Compose |
| **Android Audio Engine** | AndroidX Media3 ExoPlayer 1.4+ |
| **Android Networking** | OkHttp 4.12 with `VibeApiClient` |
| **Backend Runtime** | Node.js 24+, TypeScript 5.6, Express 4.21 |
| **Cloud Audio Storage** | Cloudinary (`vibe/music/`, video resource type, HTTP Range CDN) |
| **Database & ORM** | Supabase PostgreSQL + Prisma 5.22 |
| **Authentication** | JWT (HS256) + Supabase Auth |
| **Testing** | Vitest 2.1 (22 automated tests) |

---

## 🚀 Building & Running

### 1. Backend Setup & Run
```bash
cd backend
npm install
npm test
npm run dev
# Backend API listening on http://localhost:4000/api/v1
```

### 2. Android APK Build
```bash
export ANDROID_HOME=/home/codespace/android-sdk
export JAVA_HOME=/usr/local/sdkman/candidates/java/21.0.13.0+2-ms
./gradlew assembleDebug
```
The resulting APK is verified at:
`app/build/outputs/apk/debug/app-debug.apk` (32 MB)

---

## 📖 Complete Documentation Index

All detailed specifications and engineering documents are located in `/docs`:
- [PROJECT_STATE.md](PROJECT_STATE.md) — Live implementation status and test results
- [TASKS.md](TASKS.md) — Development task checklist
- [SESSION_HANDOFF.md](SESSION_HANDOFF.md) — Seamless handoff instructions for any AI coding agent
- [API_REFERENCE.md](API_REFERENCE.md) — Complete REST API reference and endpoints
- [BACKEND_SETUP.md](BACKEND_SETUP.md) — Backend installation, environment configuration, and migration
- [CLOUDINARY_SETUP.md](CLOUDINARY_SETUP.md) — Cloudinary cloud storage and streaming configuration
- [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md) — Supabase PostgreSQL Prisma relational schema
- [ANDROID_BACKEND_INTEGRATION.md](ANDROID_BACKEND_INTEGRATION.md) — Android remote repository and ExoPlayer streaming guide
- [SECURITY.md](SECURITY.md) — Secrets management, authentication, RBAC, and input validation
- [DEPLOYMENT.md](DEPLOYMENT.md) — Cloud backend and Android release deployment guide
- [ARCHITECTURE.md](ARCHITECTURE.md) — End-to-end architecture diagrams and layer separation
- [PRODUCT_SPEC.md](PRODUCT_SPEC.md) — Product specifications and functional requirements
- [DECISIONS.md](DECISIONS.md) — Architecture decision records (ADRs)
- [ROADMAP.md](ROADMAP.md) — Product roadmap and future enhancements
- [AGENTS.md](AGENTS.md) — Mandatory guidelines for autonomous AI agents
