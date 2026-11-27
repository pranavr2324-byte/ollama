package com.vibe.music.data.repository

import com.vibe.music.data.model.Playlist
import com.vibe.music.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class MockPlaylistRepository(
    private val songRepository: SongRepository
) : PlaylistRepository {

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())

    init {
        val allSongs = MockSongRepository.initialSongs()
        _playlists.value = listOf(
            Playlist(
                id = "pl_trending_tamil",
                title = "Trending Tamil 2024",
                description = "The hottest chartbusters across Tamil cinema and indie music right now.",
                artworkUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
                songCount = allSongs.filter { it.genre == "Tamil" }.size,
                songs = allSongs.filter { it.genre == "Tamil" },
                isUserCreated = false
            ),
            Playlist(
                id = "pl_late_night",
                title = "Late Night Chill",
                description = "Deep mood beats, warm melodies, and tranquil lo-fi for nocturnal souls.",
                artworkUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
                songCount = 4,
                songs = allSongs.filter { it.id in listOf("global_1", "global_2", "tamil_4", "global_3") },
                isUserCreated = false
            ),
            Playlist(
                id = "pl_workout",
                title = "High Octane Workout",
                description = "Heavy basslines and adrenaline pumping anthems to power your reps.",
                artworkUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
                songCount = 3,
                songs = allSongs.filter { it.id in listOf("tamil_1", "tamil_2", "tamil_6") },
                isUserCreated = false
            ),
            Playlist(
                id = "pl_focus",
                title = "Deep Focus & Study",
                description = "Instrumental textures and smooth lo-fi progressions for maximum flow state.",
                artworkUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=600&auto=format&fit=crop&q=80",
                songCount = 3,
                songs = allSongs.filter { it.id in listOf("global_2", "global_3", "tamil_5") },
                isUserCreated = false
            ),
            Playlist(
                id = "pl_my_favs",
                title = "Heavy Rotation Favorites",
                description = "Personal curated collection of everyday repeat tracks.",
                artworkUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=600&auto=format&fit=crop&q=80",
                songCount = 4,
                songs = allSongs.filter { it.isFavourite },
                isUserCreated = true
            )
        )
    }

    override fun getMoodPlaylists(): Flow<List<Playlist>> = _playlists.asStateFlow()

    override fun getUserPlaylists(): Flow<List<Playlist>> = _playlists.asStateFlow()

    override fun getAllPlaylists(): Flow<List<Playlist>> = _playlists.asStateFlow()

    override suspend fun getPlaylistById(id: String): Playlist? {
        return _playlists.value.find { it.id == id }
    }

    override suspend fun createPlaylist(title: String, description: String): Playlist {
        val newPlaylist = Playlist(
            id = "user_pl_" + UUID.randomUUID().toString().take(8),
            title = title,
            description = description.ifEmpty { "Created with VIBE" },
            artworkUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80",
            songCount = 0,
            songs = emptyList(),
            isUserCreated = true
        )
        _playlists.value = listOf(newPlaylist) + _playlists.value
        return newPlaylist
    }

    override suspend fun addSongToPlaylist(playlistId: String, song: Song): Boolean {
        var added = false
        _playlists.value = _playlists.value.map { pl ->
            if (pl.id == playlistId) {
                if (pl.songs.none { it.id == song.id }) {
                    val updatedSongs = pl.songs + song
                    added = true
                    pl.copy(songs = updatedSongs, songCount = updatedSongs.size)
                } else pl
            } else pl
        }
        return added
    }

    override suspend fun removeSongFromPlaylist(playlistId: String, songId: String): Boolean {
        var removed = false
        _playlists.value = _playlists.value.map { pl ->
            if (pl.id == playlistId) {
                val updatedSongs = pl.songs.filterNot { it.id == songId }
                if (updatedSongs.size != pl.songs.size) {
                    removed = true
                    pl.copy(songs = updatedSongs, songCount = updatedSongs.size)
                } else pl
            } else pl
        }
        return removed
    }

    override suspend fun deletePlaylist(playlistId: String): Boolean {
        val count = _playlists.value.size
        _playlists.value = _playlists.value.filterNot { it.id == playlistId }
        return _playlists.value.size < count
    }

    override suspend fun searchPlaylists(query: String): List<Playlist> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        return _playlists.value.filter { it.title.lowercase().contains(q) }
    }
}
