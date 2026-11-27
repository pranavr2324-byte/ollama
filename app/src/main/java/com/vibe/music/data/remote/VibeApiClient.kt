package com.vibe.music.data.remote

import com.vibe.music.data.model.Album
import com.vibe.music.data.model.Artist
import com.vibe.music.data.model.Playlist
import com.vibe.music.data.model.Song
import com.vibe.music.viewmodel.AuthUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: String? = null, val cause: Throwable? = null) : NetworkResult<Nothing>()
}

class VibeApiClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private fun buildRequest(path: String, method: String = "GET", bodyJson: String? = null): Request {
        val base = VibeNetworkConfig.baseUrl
        val fullUrl = if (path.startsWith("http")) path else "$base${path.removePrefix("/")}"

        val builder = Request.Builder()
            .url(fullUrl)
            .header("Accept", "application/json")

        VibeNetworkConfig.authToken?.let { token ->
            builder.header("Authorization", "Bearer $token")
        }

        when (method.uppercase()) {
            "POST" -> builder.post((bodyJson ?: "{}").toRequestBody(jsonMediaType))
            "PATCH" -> builder.patch((bodyJson ?: "{}").toRequestBody(jsonMediaType))
            "DELETE" -> {
                if (bodyJson != null) {
                    builder.delete(bodyJson.toRequestBody(jsonMediaType))
                } else {
                    builder.delete()
                }
            }
            else -> builder.get()
        }

        return builder.build()
    }

    private suspend fun executeCall(request: Request): JSONObject = withContext(Dispatchers.IO) {
        val response = client.newCall(request).execute()
        val bodyString = response.body?.string() ?: throw IOException("Empty response body from server")

        if (!response.isSuccessful) {
            val errJson = try { JSONObject(bodyString) } catch (_: Exception) { null }
            val errMsg = errJson?.optJSONObject("error")?.optString("message")
                ?: "HTTP ${response.code}: ${response.message}"
            throw IOException(errMsg)
        }

        JSONObject(bodyString)
    }

    // ==========================================
    // AUTHENTICATION
    // ==========================================

    suspend fun login(email: String, password: String): NetworkResult<AuthUser> = runCatching {
        val payload = JSONObject().apply {
            put("email", email)
            put("password", password)
        }
        val root = executeCall(buildRequest("auth/login", "POST", payload.toString()))
        val data = root.getJSONObject("data")
        val token = data.getString("token")
        VibeNetworkConfig.authToken = token
        parseAuthUser(data.getJSONObject("user"))
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Login failed", cause = it) }
    )

    suspend fun register(name: String, email: String, password: String): NetworkResult<AuthUser> = runCatching {
        val payload = JSONObject().apply {
            put("name", name)
            put("email", email)
            put("password", password)
        }
        val root = executeCall(buildRequest("auth/register", "POST", payload.toString()))
        val data = root.getJSONObject("data")
        val token = data.getString("token")
        VibeNetworkConfig.authToken = token
        parseAuthUser(data.getJSONObject("user"))
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Registration failed", cause = it) }
    )

    suspend fun logout(): NetworkResult<Unit> = runCatching {
        executeCall(buildRequest("auth/logout", "POST"))
        Unit
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Logout failed", cause = it) }
    )

    suspend fun getMe(): NetworkResult<AuthUser> = runCatching {
        val root = executeCall(buildRequest("auth/me"))
        val data = root.getJSONObject("data")
        parseAuthUser(data.getJSONObject("user"))
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Failed to get user", cause = it) }
    )

    private fun parseAuthUser(obj: JSONObject) = AuthUser(
        id = obj.getString("id"),
        email = obj.getString("email"),
        name = obj.optString("name", obj.optString("email", "User")),
        role = obj.optString("role", "USER")
    )

    // ==========================================
    // SONGS & STREAMING
    // ==========================================

    suspend fun getSongs(
        genre: String? = null,
        limit: Int = 30,
        page: Int = 1
    ): NetworkResult<List<Song>> = runCatching {
        var query = "songs?page=$page&limit=$limit"
        if (!genre.isNullOrBlank()) {
            query += "&genre=${java.net.URLEncoder.encode(genre, "UTF-8")}"
        }
        val root = executeCall(buildRequest(query))
        val items = root.getJSONObject("data").getJSONArray("items")
        parseSongs(items)
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Failed to fetch songs", cause = it) }
    )

    suspend fun getSongById(id: String): NetworkResult<Song> = runCatching {
        val root = executeCall(buildRequest("songs/$id"))
        val songObj = root.getJSONObject("data").getJSONObject("song")
        parseSong(songObj)
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Failed to fetch song", cause = it) }
    )

    suspend fun getStreamUrl(id: String): NetworkResult<String> = runCatching {
        val root = executeCall(buildRequest("songs/$id/stream"))
        root.getJSONObject("data").getString("streamUrl")
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Failed to get stream URL", cause = it) }
    )

    // ==========================================
    // ALBUMS
    // ==========================================

    suspend fun getAlbums(limit: Int = 20, page: Int = 1): NetworkResult<List<Album>> = runCatching {
        val root = executeCall(buildRequest("albums?page=$page&limit=$limit"))
        val items = root.getJSONObject("data").getJSONArray("items")
        parseAlbums(items)
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Failed to fetch albums", cause = it) }
    )

    suspend fun getAlbumById(id: String): NetworkResult<Album> = runCatching {
        val root = executeCall(buildRequest("albums/$id"))
        val albObj = root.getJSONObject("data").getJSONObject("album")
        parseAlbum(albObj)
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Failed to fetch album", cause = it) }
    )

    // ==========================================
    // ARTISTS
    // ==========================================

    suspend fun getArtists(limit: Int = 20, page: Int = 1): NetworkResult<List<Artist>> = runCatching {
        val root = executeCall(buildRequest("artists?page=$page&limit=$limit"))
        val items = root.getJSONObject("data").getJSONArray("items")
        parseArtists(items)
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Failed to fetch artists", cause = it) }
    )

    suspend fun getArtistById(id: String): NetworkResult<Artist> = runCatching {
        val root = executeCall(buildRequest("artists/$id"))
        val artObj = root.getJSONObject("data").getJSONObject("artist")
        parseArtist(artObj)
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Failed to fetch artist", cause = it) }
    )

    // ==========================================
    // SEARCH
    // ==========================================

    data class SearchResults(
        val songs: List<Song>,
        val albums: List<Album>,
        val artists: List<Artist>
    )

    suspend fun search(query: String): NetworkResult<SearchResults> = runCatching {
        val encoded = java.net.URLEncoder.encode(query, "UTF-8")
        val root = executeCall(buildRequest("search?q=$encoded"))
        val data = root.getJSONObject("data")
        SearchResults(
            songs = parseSongs(data.optJSONArray("songs") ?: JSONArray()),
            albums = parseAlbums(data.optJSONArray("albums") ?: JSONArray()),
            artists = parseArtists(data.optJSONArray("artists") ?: JSONArray())
        )
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Search failed", cause = it) }
    )

    // ==========================================
    // PLAYLISTS
    // ==========================================

    suspend fun getPlaylists(): NetworkResult<List<Playlist>> = runCatching {
        val root = executeCall(buildRequest("playlists"))
        val arr = root.getJSONObject("data").getJSONArray("playlists")
        val list = mutableListOf<Playlist>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(
                Playlist(
                    id = obj.getString("id"),
                    title = obj.getString("name"),
                    description = obj.optString("description").takeIf { it != "null" } ?: "",
                    artworkUrl = obj.optString("artworkUrl").takeIf { it.isNotBlank() && it != "null" }
                        ?: "https://images.unsplash.com/photo-1514525253161-7a46d19cd819",
                    songCount = obj.optInt("songCount", 0),
                    isUserCreated = true
                )
            )
        }
        list
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Failed to fetch playlists", cause = it) }
    )

    suspend fun getPlaylistById(id: String): NetworkResult<Playlist> = runCatching {
        val root = executeCall(buildRequest("playlists/$id"))
        val obj = root.getJSONObject("data").getJSONObject("playlist")
        val songsArr = obj.optJSONArray("songs") ?: JSONArray()
        Playlist(
            id = obj.getString("id"),
            title = obj.getString("name"),
            description = obj.optString("description").takeIf { it != "null" } ?: "",
            artworkUrl = obj.optString("artworkUrl").takeIf { it.isNotBlank() && it != "null" }
                ?: "https://images.unsplash.com/photo-1514525253161-7a46d19cd819",
            songCount = obj.optInt("songCount", songsArr.length()),
            songs = parseSongs(songsArr),
            isUserCreated = true
        )
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Failed to fetch playlist", cause = it) }
    )

    suspend fun createPlaylist(name: String, description: String = ""): NetworkResult<Playlist> = runCatching {
        val payload = JSONObject().apply {
            put("name", name)
            if (description.isNotBlank()) put("description", description)
        }
        val root = executeCall(buildRequest("playlists", "POST", payload.toString()))
        val obj = root.getJSONObject("data").getJSONObject("playlist")
        Playlist(
            id = obj.getString("id"),
            title = obj.getString("name"),
            description = obj.optString("description").takeIf { it != "null" } ?: "",
            artworkUrl = obj.optString("artworkUrl").takeIf { it.isNotBlank() && it != "null" }
                ?: "https://images.unsplash.com/photo-1514525253161-7a46d19cd819",
            songCount = 0,
            isUserCreated = true
        )
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Failed to create playlist", cause = it) }
    )

    suspend fun addSongToPlaylist(playlistId: String, songId: String): NetworkResult<Boolean> = runCatching {
        val payload = JSONObject().apply { put("songId", songId) }
        executeCall(buildRequest("playlists/$playlistId/songs", "POST", payload.toString()))
        true
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Failed to add song", cause = it) }
    )

    suspend fun removeSongFromPlaylist(playlistId: String, songId: String): NetworkResult<Boolean> = runCatching {
        executeCall(buildRequest("playlists/$playlistId/songs/$songId", "DELETE"))
        true
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Failed to remove song", cause = it) }
    )

    suspend fun deletePlaylist(playlistId: String): NetworkResult<Boolean> = runCatching {
        executeCall(buildRequest("playlists/$playlistId", "DELETE"))
        true
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Failed to delete playlist", cause = it) }
    )

    // ==========================================
    // FAVOURITES & RECENTLY PLAYED
    // ==========================================

    suspend fun getFavourites(): NetworkResult<List<Song>> = runCatching {
        val root = executeCall(buildRequest("favourites"))
        val arr = root.getJSONObject("data").getJSONArray("songs")
        parseSongs(arr)
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Failed to fetch favourites", cause = it) }
    )

    suspend fun toggleFavourite(songId: String, currentStatus: Boolean): NetworkResult<Boolean> = runCatching {
        if (currentStatus) {
            executeCall(buildRequest("favourites/$songId", "DELETE"))
            false
        } else {
            executeCall(buildRequest("favourites/$songId", "POST"))
            true
        }
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Failed to toggle favourite", cause = it) }
    )

    suspend fun getRecentlyPlayed(): NetworkResult<List<Song>> = runCatching {
        val root = executeCall(buildRequest("recently-played"))
        val arr = root.getJSONObject("data").getJSONArray("items")
        parseSongs(arr)
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Failed to fetch recently played", cause = it) }
    )

    suspend fun recordRecentlyPlayed(songId: String): NetworkResult<Unit> = runCatching {
        executeCall(buildRequest("recently-played/$songId", "POST"))
        Unit
    }.fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { NetworkResult.Error(it.message ?: "Failed to record playback", cause = it) }
    )

    // ==========================================
    // JSON PARSERS
    // ==========================================

    fun parseSong(obj: JSONObject): Song {
        val artwork = obj.optString("artworkUrl").takeIf { it.isNotBlank() && it != "null" }
            ?: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4"
        val artist = obj.optString("artistName").takeIf { it.isNotBlank() && it != "null" } ?: "Unknown Artist"
        val album = obj.optString("albumTitle").takeIf { it.isNotBlank() && it != "null" } ?: "Single"
        val genre = obj.optString("genre").takeIf { it.isNotBlank() && it != "null" } ?: "Various"

        return Song(
            id = obj.getString("id"),
            title = obj.getString("title"),
            artist = artist,
            album = album,
            durationMs = obj.optLong("durationMs", obj.optLong("durationSeconds", 180) * 1000),
            artworkUrl = artwork,
            streamUrl = obj.optString("streamUrl").takeIf { it.isNotBlank() && it != "null" },
            isFavourite = obj.optBoolean("isFavourite", false),
            playCount = obj.optLong("playCount", 0),
            genre = genre,
            year = 2024
        )
    }

    fun parseSongs(arr: JSONArray): List<Song> {
        val list = mutableListOf<Song>()
        for (i in 0 until arr.length()) {
            list.add(parseSong(arr.getJSONObject(i)))
        }
        return list
    }

    fun parseAlbum(obj: JSONObject): Album {
        val songsArr = obj.optJSONArray("songs") ?: JSONArray()
        val artwork = obj.optString("artworkUrl").takeIf { it.isNotBlank() && it != "null" }
            ?: "https://images.unsplash.com/photo-1514525253161-7a46d19cd819"
        val artist = obj.optString("artistName").takeIf { it.isNotBlank() && it != "null" } ?: "Unknown Artist"
        val genre = obj.optString("genre").takeIf { it.isNotBlank() && it != "null" } ?: "Various"

        return Album(
            id = obj.getString("id"),
            title = obj.getString("title"),
            artist = artist,
            artworkUrl = artwork,
            year = obj.optInt("year", 2024),
            songCount = obj.optInt("songCount", songsArr.length()),
            songs = parseSongs(songsArr),
            genre = genre
        )
    }

    fun parseAlbums(arr: JSONArray): List<Album> {
        val list = mutableListOf<Album>()
        for (i in 0 until arr.length()) {
            list.add(parseAlbum(arr.getJSONObject(i)))
        }
        return list
    }

    fun parseArtist(obj: JSONObject): Artist {
        val popArr = obj.optJSONArray("popularSongs") ?: JSONArray()
        val albArr = obj.optJSONArray("albums") ?: JSONArray()
        val image = obj.optString("artworkUrl").takeIf { it.isNotBlank() && it != "null" }
            ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb"
        val listeners = obj.optString("monthlyListeners").takeIf { it.isNotBlank() && it != "null" }
            ?: "1.2M Monthly Listeners"

        return Artist(
            id = obj.getString("id"),
            name = obj.getString("name"),
            imageUrl = image,
            monthlyListeners = listeners,
            bio = obj.optString("biography").takeIf { it != "null" } ?: "",
            popularSongs = parseSongs(popArr),
            albums = parseAlbums(albArr)
        )
    }

    fun parseArtists(arr: JSONArray): List<Artist> {
        val list = mutableListOf<Artist>()
        for (i in 0 until arr.length()) {
            list.add(parseArtist(arr.getJSONObject(i)))
        }
        return list
    }
}
