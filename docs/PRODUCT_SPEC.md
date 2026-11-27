# VIBE — Product Specification

---

## 🎯 1. Product Vision

**VIBE** is a premium, nocturnal, distraction-free music streaming platform built for music purists. It combines an ultra-fast Jetpack Compose Android client with a robust Cloudinary-backed audio streaming backend and Supabase PostgreSQL metadata catalog.

---

## 🎧 2. Implemented Features

### Android Client
- **Home Hub**: Featured releases, Trending Tamil hits, Recently Played, Recommended Albums, Popular Artists, Mood Playlists.
- **Explore & Discovery**: Category chips, New Releases carousel, 8-tile genre grid with vibrant gradients, Curated Playlists.
- **Reactive Search**: Debounced search across songs, albums, and artists, category filter chips (All, Songs, Albums, Artists, Playlists), search history chips with delete/clear.
- **Music Library**: Tabbed navigation (Songs, Albums, Artists, Playlists, Favourites) and real-time custom playlist creation.
- **Full Player**: Vinyl spin animation, ambient glow, scrubbable seek slider with elapsed/remaining times, Play/Pause, Next/Previous, Shuffle, Repeat 1/All, Favourite toggle, Queue bottom sheet trigger, interactive volume slider.
- **Persistent Mini Player**: Anchored bottom player with thumbnail, title, artist, playback controls, and micro progress bar across all top destinations.
- **Live Playback Queue**: Modal sheet, animated equalizer, track reordering, track removal, clear queue, and tap to skip.
- **Settings Screen**: Streaming quality, download quality, theme preferences, gapless toggle, volume normalization, user profile card.

### Backend & Cloud Storage
- **Cloudinary Storage**: High-fidelity audio storage under `vibe/music/`, HTTP `Range` request delivery for seekable playback in ExoPlayer, signed delivery URLs, asset metadata, deletion.
- **Supabase PostgreSQL Schema**: Prisma schema covering users, artists, albums, songs, ordered playlists (`playlist_songs`), favourites, and recently played.
- **Authentication**: JWT token issuance and verification, Supabase Auth integration, RBAC (`USER` vs `ADMIN`).
- **REST APIs**: Full CRUD for music catalog, search, streaming, playlists, favourites, recently played, and administrative uploads.
- **Security & Reliability**: Zod validation, Helmet security headers, rate limiting, Multer streaming upload with temporary disk buffering and automatic rollback on failure.

---

## 📱 3. User Flows

1. **Browse & Stream**:
   - User opens Home or Explore.
   - User taps a track or "Play All" on an album.
   - Remote repository resolves track with Cloudinary `streamUrl`.
   - ExoPlayer streams audio with HTTP range seeking; mini player and full player reflect live state.
2. **Search**:
   - User types query in Search bar.
   - Debounced API queries `/api/v1/search?q={query}`.
   - Results display categorised songs, albums, and artists.
3. **Playlists & Favourites**:
   - User taps Heart icon: calls `POST/DELETE /api/v1/favourites/:songId`, updates in-memory and cloud state.
   - User creates playlist: persists in Supabase via `/api/v1/playlists`.
   - User reorders or adds songs to playlist: updates ordered `playlist_songs` positions.
