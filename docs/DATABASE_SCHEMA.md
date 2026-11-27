# VIBE — Database Schema (Supabase PostgreSQL / Prisma)

> **ORM:** Prisma (`backend/prisma/schema.prisma`)
> **Database:** PostgreSQL (Supabase compatible)

---

## 🗄️ Relational Tables

### 1. `users`
Stores authenticated user profiles, Supabase linkage, and role-based access control.
- `id` (UUID, PK)
- `email` (String, Unique)
- `name` (String, Nullable)
- `password_hash` (String, Nullable — used for local JWT auth when Supabase is not configured)
- `supabase_id` (String, Unique, Nullable)
- `role` (`USER` | `ADMIN`, Default: `USER`)
- `created_at`, `updated_at` (DateTime)

### 2. `artists`
- `id` (UUID, PK)
- `name` (String, Indexed)
- `biography` (String, Nullable)
- `artwork_url` (String, Nullable)
- `monthly_listeners` (String, Default: `"0 Monthly Listeners"`)
- `created_at` (DateTime)

### 3. `albums`
- `id` (UUID, PK)
- `title` (String, Indexed)
- `artist_id` (UUID, FK → `artists.id`, `ON DELETE CASCADE`, Indexed)
- `artwork_url` (String, Nullable)
- `release_date` (DateTime, Nullable)
- `genre` (String, Default: `"Various"`)
- `created_at` (DateTime)

### 4. `songs`
Stores track metadata and Cloudinary asset identifiers (never binary audio blobs).
- `id` (UUID, PK)
- `title` (String, Indexed)
- `artist_id` (UUID, FK → `artists.id`, `ON DELETE CASCADE`, Indexed)
- `album_id` (UUID, FK → `albums.id`, `ON DELETE SET NULL`, Nullable, Indexed)
- `cloudinary_public_id` (String, Unique)
- `cloudinary_resource_type` (String, Default: `"video"`)
- `original_filename` (String, Nullable)
- `audio_format` (String, Nullable)
- `duration_seconds` (Int, Default: `0`)
- `file_size_bytes` (BigInt, Nullable)
- `bitrate` (Int, Nullable)
- `artwork_url` (String, Nullable)
- `genre` (String, Default: `"Various"`, Indexed)
- `release_date` (DateTime, Nullable)
- `is_published` (Boolean, Default: `true`, Indexed)
- `play_count` (BigInt, Default: `0`)
- `created_at`, `updated_at` (DateTime)

### 5. `playlists`
- `id` (UUID, PK)
- `user_id` (UUID, FK → `users.id`, `ON DELETE CASCADE`, Indexed)
- `name` (String)
- `description` (String, Nullable)
- `artwork_url` (String, Nullable)
- `created_at`, `updated_at` (DateTime)

### 6. `playlist_songs`
Preserves explicit ordering of tracks inside playlists.
- `playlist_id` (UUID, FK → `playlists.id`, `ON DELETE CASCADE`)
- `song_id` (UUID, FK → `songs.id`, `ON DELETE CASCADE`)
- `position` (Int)
- `added_at` (DateTime)
- **Primary Key**: `(playlist_id, song_id)`
- **Index**: `(playlist_id, position)`

### 7. `favourites`
- `user_id` (UUID, FK → `users.id`, `ON DELETE CASCADE`)
- `song_id` (UUID, FK → `songs.id`, `ON DELETE CASCADE`)
- `created_at` (DateTime)
- **Primary Key**: `(user_id, song_id)`
- **Index**: `(user_id)`

### 8. `recently_played`
- `id` (UUID, PK)
- `user_id` (UUID, FK → `users.id`, `ON DELETE CASCADE`)
- `song_id` (UUID, FK → `songs.id`, `ON DELETE CASCADE`)
- `played_at` (DateTime)
- **Index**: `(user_id, played_at DESC)`
