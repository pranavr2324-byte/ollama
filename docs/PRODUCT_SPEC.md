# VIBE — Product Specification

---

## 🎯 Vision & Scope

**VIBE** is a modern cloud-based music streaming frontend built natively for Android. The application delivers an uncompromising audio-centric experience, rejecting non-music clutter (no news feeds, social posts, or video streams).

---

## 📱 Functional Screen Specifications

### 1. Home Screen
- **Personalized Greeting**: Dynamic morning/afternoon/evening header with user profile avatar.
- **Featured Hero Card**: Highlighting top trending release with one-touch playback.
- **Trending Tamil Music**: Curated carousel featuring top Tamil hits (Anirudh Ravichander, A.R. Rahman, Yuvan Shankar Raja, Santhosh Narayanan, Sid Sriram).
- **Recently Played**: Quick horizontal scroll of recently listened tracks.
- **Recommended Albums & Popular Artists**: Circular artist avatars and rectangular album cards.
- **Mood Playlists**: "Late Night Chill", "High Octane Workout", "Deep Focus", "Acoustic Sunset".

### 2. Explore Screen
- Quick category tags: "All", "Trending", "New Releases", "Tamil Hits", "Indie", "Hip Hop".
- New Releases carousel with instant play buttons.
- Dynamic Genre Grid with gradient cards (Synthwave, Tamil Indie, Lo-Fi, EDM, Pop, Rock, Classical).
- Curated editor playlists and featured spotlight artists.

### 3. Search Screen
- Reactive instant search input with clear button.
- Filter chips: All, Songs, Albums, Artists, Playlists.
- Search history with chip removal and "Clear All".
- Rich result rows with artwork, metadata, and action buttons.
- Intuitive empty states for zero results and initial search state.

### 4. Music Library Screen
- Top tab navigation:
  - **All Songs**: Complete list with duration, artist, and menu.
  - **Albums**: Grid of album cards with track count and release year.
  - **Artists**: Grid of artist circles with follower counts.
  - **Playlists**: User playlists + "Create New Playlist" floating action.
  - **Favourites**: Songs marked as favourite.
  - **Recently Played**: History log of played tracks.

### 5. Detail Screens
- **Album Screen**: Parallax artwork header, release year, track count, total duration, Play All, Shuffle, and track listing.
- **Artist Screen**: Hero artist portrait, listener stats, top 5 popular tracks, albums discography, and play controls.
- **Playlist Screen**: Custom playlist artwork, edit title, add/remove tracks dialog, play/shuffle controls.

### 6. Full-Screen Audio Player
- Expands seamlessly from mini player.
- High-resolution artwork with subtle ambient glow and animated vinyl spin during playback.
- Track title, artist name, and favourite heart toggle.
- Interactive scrubbable progress bar with elapsed and remaining timestamps.
- Playback controls: Shuffle toggle, Previous track, Play/Pause with pulsing effect, Next track, Repeat (Off / All / One) toggle.
- Interactive volume slider.
- Bottom sheet trigger for the live playback queue.

### 7. Persistent Mini Player
- Visible across all top-level tabs when audio is loaded.
- Mini album thumbnail, song title, and artist marquee.
- Inline Play/Pause toggle and Next track button.
- Micro progress bar along the bottom edge.
- Tap opens the full-screen player.

### 8. Live Playback Queue
- Displays currently playing track and upcoming queue.
- Reorder items, remove individual tracks, clear queue.
- Tap any queue item to skip immediately to that track.

### 9. Settings Screen
- Audio streaming quality (Normal 128kbps, High 256kbps, Lossless 320kbps).
- Download quality options.
- Visual theme preferences (Charcoal Black default, Obsidian Glow, Neon Cyber).
- Equalizer preview, cache clearing, app version, and open-source licenses.
