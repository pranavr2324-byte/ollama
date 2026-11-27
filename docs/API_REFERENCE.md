# VIBE — REST API Reference

Base URL: `http://localhost:4000/api/v1` (or `http://10.0.2.2:4000/api/v1` from Android emulator)

All endpoints return JSON responses adhering to the format:
```json
{
  "success": true,
  "data": { ... }
}
```
Error responses return:
```json
{
  "success": false,
  "error": {
    "message": "Error description",
    "code": "ERROR_CODE",
    "details": []
  }
}
```

---

## 🔐 1. Authentication (`/api/v1/auth`)

### `POST /auth/register`
Registers a new user.
- **Body**: `{ "email": "user@vibe.music", "password": "Password123!", "name": "Vibe Listener" }`
- **Response**: `201 Created` with `{ "token": "jwt...", "user": { "id", "email", "name", "role" } }`

### `POST /auth/login`
Authenticates with email and password.
- **Body**: `{ "email": "user@vibe.music", "password": "Password123!" }`
- **Response**: `200 OK` with `{ "token": "jwt...", "user": { "id", "email", "name", "role" } }`

### `POST /auth/logout`
Invalidates session.
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK`

### `GET /auth/me`
Retrieves authenticated user profile.
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` with user profile, `playlistCount`, and `favouriteCount`.

---

## 🎵 2. Music Catalog & Streaming (`/api/v1`)

### `GET /songs`
Lists published songs with pagination and filtering.
- **Query params**: `page=1`, `limit=30`, `genre=Tamil`, `artistId=uuid`, `albumId=uuid`, `sortBy=createdAt`, `order=desc`
- **Response**: `200 OK` with items (including `streamUrl`, `isFavourite`), pagination metadata.

### `GET /songs/:id`
Retrieves a single song by UUID.
- **Response**: `200 OK` with song details and `streamUrl`.

### `GET /songs/:id/stream`
Retrieves the Cloudinary audio delivery URL or redirects.
- **Query params**: `redirect=true` (optional 302 redirect directly to Cloudinary CDN)
- **Response**: `200 OK` with `{ "streamUrl": "https://res.cloudinary.com/...", "format": "mp3", "durationSeconds": 210 }`

### `GET /albums`
Lists albums with pagination.
- **Query params**: `page=1`, `limit=20`, `genre=Tamil`, `artistId=uuid`
- **Response**: `200 OK` with album cards and track counts.

### `GET /albums/:id`
Retrieves album details and track list.
- **Response**: `200 OK` with album metadata, artist name, and list of songs with `streamUrl`.

### `GET /artists`
Lists artists with pagination.
- **Query params**: `page=1`, `limit=20`
- **Response**: `200 OK` with artist avatars and listener counts.

### `GET /artists/:id`
Retrieves artist details, popular tracks, and albums.
- **Response**: `200 OK` with artist biography, popular songs, and discography.

### `GET /search?q={query}`
Searches across songs, albums, and artists.
- **Query params**: `q=tamil`, `genre=Pop`, `limit=20`
- **Response**: `200 OK` with `{ songs: [...], albums: [...], artists: [...] }`.

---

## 📚 3. Library: Playlists, Favourites & History (`/api/v1`)

### `GET /playlists`
Lists user-owned playlists.
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` with playlists array and track counts.

### `POST /playlists`
Creates a new playlist.
- **Headers**: `Authorization: Bearer <token>`
- **Body**: `{ "name": "Late Night Vibes", "description": "Chill beats" }`
- **Response**: `201 Created`

### `GET /playlists/:id`
Retrieves playlist details and ordered song list.
- **Response**: `200 OK` with playlist metadata and songs ordered by position.

### `PATCH /playlists/:id`
Updates playlist metadata.
- **Headers**: `Authorization: Bearer <token>`
- **Body**: `{ "name": "Updated Name", "description": "New description" }`
- **Response**: `200 OK`

### `DELETE /playlists/:id`
Deletes a playlist (owner or admin only).
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK`

### `POST /playlists/:id/songs`
Adds a song to the end of the playlist.
- **Headers**: `Authorization: Bearer <token>`
- **Body**: `{ "songId": "uuid" }`
- **Response**: `201 Created` with next position.

### `DELETE /playlists/:id/songs/:songId`
Removes a song from the playlist.
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK`

### `GET /favourites`
Lists authenticated user's favourite songs.
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` with song array (`isFavourite: true`, `streamUrl`).

### `POST /favourites/:songId`
Adds song to user's favourites.
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `201 Created`

### `DELETE /favourites/:songId`
Removes song from favourites.
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK`

### `GET /recently-played`
Retrieves recently played tracks for the authenticated user.
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `200 OK` with items and playback timestamps.

### `POST /recently-played/:songId`
Records song playback and increments song play count.
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `201 Created`

---

## 🛡️ 4. Administration (`/api/v1/admin`)

Requires `Authorization: Bearer <admin-token>`.

### `POST /admin/songs/upload`
Uploads an audio file to Cloudinary and saves metadata to PostgreSQL.
- **Content-Type**: `multipart/form-data`
- **Fields**:
  - `audio` (file, max 50 MB, formats: `.mp3`, `.m4a`, `.aac`, `.wav`, `.flac`, `.ogg`)
  - `title` (string)
  - `artistName` or `artistId` (string)
  - `albumTitle` or `albumId` (optional string)
  - `genre` (optional string)
  - `artworkUrl` (optional string)
  - `isPublished` (optional boolean, default `true`)
- **Response**: `201 Created` with created song object.

### `PATCH /admin/songs/:id`
Updates song metadata.
- **Body**: `{ "title": "New Title", "genre": "Tamil Pop", "isPublished": true }`
- **Response**: `200 OK`

### `DELETE /admin/songs/:id`
Deletes a song from PostgreSQL and deletes the Cloudinary audio asset.
- **Response**: `200 OK`

### `POST /admin/songs/:id/publish`
Publishes or unpublishes a track.
- **Body**: `{ "isPublished": true }`
- **Response**: `200 OK`
