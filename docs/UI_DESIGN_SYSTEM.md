# VIBE — UI Design System Specification

---

## 🎨 Aesthetic Direction

VIBE embodies a nocturnal, luxurious, audio-centric visual language. It avoids sterile white backgrounds and standard card templates, opting instead for deep black/charcoal canvas with luminous accent gradients, glassmorphism, and high-contrast typography.

---

## 🌈 Color Palette

```kotlin
// Primary Backgrounds & Surfaces
val VibeBackground = Color(0xFF0A0A0E)      // Deepest Obsidian
val VibeSurface = Color(0xFF14141B)         // Charcoal Surface
val VibeSurfaceElevated = Color(0xFF1F1F2A) // Elevated Card / Modal
val VibeSurfaceGlass = Color(0x33282838)    // Translucent Glass (20% opacity)

// Accent Neons & Gradients
val VibePrimary = Color(0xFF8B5CF6)         // Electric Violet
val VibeSecondary = Color(0xFF06B6D4)       // Cyber Cyan
val VibeAccentPink = Color(0xFFEC4899)      // Hot Magenta
val VibeAccentGreen = Color(0xFF10B981)     // Acid Emerald

// Typography Colors
val VibeTextPrimary = Color(0xFFF8FAFC)     // Crisp White
val VibeTextSecondary = Color(0xFF94A3B8)   // Slate Gray
val VibeTextMuted = Color(0xFF64748B)       // Muted Dark Slate
```

---

## 🪟 Glassmorphism & Materials

- **GlassCard**: Custom border gradient (`0.5.dp`), surface tint with 30% alpha, blur simulation, and rounded corners (`16.dp` to `24.dp`).
- **Ambient Glow**: Album artwork emits an understated blurred color halo matching dominant tones.
- **Micro-Animations**: Smooth alpha crossfades, rotation of vinyl record during active playback, bounce effect on play/pause press.

---

## 🔠 Typography Hierarchy

| Style | Size | Weight | Usage |
|---|---|---|---|
| `displayLarge` | 32sp | Bold (800) | Full Player Track Title, Hero Headers |
| `headlineMedium` | 22sp | SemiBold (700) | Section Titles ("Trending Tamil", "Albums") |
| `titleMedium` | 16sp | SemiBold (600) | Track Card Titles, Playlist Names |
| `bodyMedium` | 14sp | Regular (400) | Artist Names, Subtitles, Duration |
| `labelSmall` | 11sp | Medium (500) | Badges, Timestamps, Mini Player Subtitle |

---

## 🧩 Reusable Core Components

1. `VibeGlassCard`: Semi-transparent card with gradient border for hero items and mood playlists.
2. `SongListItem`: Compact row with artwork, title, artist, duration, and menu trigger.
3. `AlbumCard`: Square artwork card with rounded corners, subtle drop shadow, and play badge.
4. `ArtistAvatar`: Circular image with neon border on selection.
5. `MiniPlayer`: Floating anchored card with micro progress bar and playback buttons.
6. `GradientPlayButton`: Vibrant circular CTA for primary actions.
