package com.vibe.music.data.repository

import com.vibe.music.R
import com.vibe.music.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class MockSongRepository : SongRepository {

    private val _songs = MutableStateFlow<List<Song>>(initialSongs())
    private val _recentlyPlayedIds = MutableStateFlow<List<String>>(
        listOf("tamil_1", "tamil_2", "tamil_4", "global_1")
    )

    override fun getAllSongs(): Flow<List<Song>> = _songs.asStateFlow()

    override fun getFeaturedSongs(): Flow<List<Song>> = _songs.map { list ->
        list.filter { it.id in listOf("tamil_1", "tamil_2", "global_2", "tamil_5") }
    }

    override fun getTrendingTamilSongs(): Flow<List<Song>> = _songs.map { list ->
        list.filter { it.genre == "Tamil" }
    }

    override fun getRecentlyPlayed(): Flow<List<Song>> = _recentlyPlayedIds.map { ids ->
        val currentList = _songs.value
        ids.mapNotNull { id -> currentList.find { it.id == id } }
    }

    override fun getFavouriteSongs(): Flow<List<Song>> = _songs.map { list ->
        list.filter { it.isFavourite }
    }

    override suspend fun getSongById(id: String): Song? {
        return _songs.value.find { it.id == id }
    }

    override suspend fun toggleFavourite(songId: String): Boolean {
        var isFav = false
        val updated = _songs.value.map { song ->
            if (song.id == songId) {
                isFav = !song.isFavourite
                song.copy(isFavourite = isFav)
            } else song
        }
        _songs.value = updated
        return isFav
    }

    override suspend fun recordPlay(songId: String) {
        val currentIds = _recentlyPlayedIds.value.toMutableList()
        currentIds.remove(songId)
        currentIds.add(0, songId)
        _recentlyPlayedIds.value = currentIds.take(20)

        _songs.value = _songs.value.map {
            if (it.id == songId) it.copy(playCount = it.playCount + 1) else it
        }
    }

    override suspend fun searchSongs(query: String): List<Song> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        return _songs.value.filter {
            it.title.lowercase().contains(q) ||
            it.artist.lowercase().contains(q) ||
            it.album.lowercase().contains(q)
        }
    }

    companion object {
        fun initialSongs(): List<Song> = listOf(
            Song(
                id = "tamil_1",
                title = "Naa Ready",
                artist = "Anirudh Ravichander, Thalapathy Vijay",
                album = "Leo",
                durationMs = 248000,
                artworkUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
                audioResId = R.raw.sample_tamil_melody,
                isFavourite = true,
                genre = "Tamil",
                year = 2023
            ),
            Song(
                id = "tamil_2",
                title = "Hukum - Thalaivar Alappara",
                artist = "Anirudh Ravichander",
                album = "Jailer",
                durationMs = 207000,
                artworkUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
                audioResId = R.raw.sample_electronic_bass,
                isFavourite = true,
                genre = "Tamil",
                year = 2023
            ),
            Song(
                id = "tamil_3",
                title = "Katchi Sera",
                artist = "Sai Abhyankkar",
                album = "Katchi Sera Single",
                durationMs = 192000,
                artworkUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=600&auto=format&fit=crop&q=80",
                audioResId = R.raw.sample_tamil_melody,
                isFavourite = false,
                genre = "Tamil",
                year = 2024
            ),
            Song(
                id = "tamil_4",
                title = "Marakkuma Nenjam",
                artist = "A.R. Rahman",
                album = "Vendhu Thanindhathu Kaadu",
                durationMs = 255000,
                artworkUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
                audioResId = R.raw.sample_acoustic_guitar,
                isFavourite = true,
                genre = "Tamil",
                year = 2022
            ),
            Song(
                id = "tamil_5",
                title = "Aasa Kooda",
                artist = "Sai Abhyankkar, TheRoute",
                album = "Aasa Kooda Single",
                durationMs = 210000,
                artworkUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=600&auto=format&fit=crop&q=80",
                audioResId = R.raw.sample_chill_lofi,
                isFavourite = false,
                genre = "Tamil",
                year = 2024
            ),
            Song(
                id = "tamil_6",
                title = "Arabic Kuthu",
                artist = "Anirudh Ravichander, Jonita Gandhi",
                album = "Beast",
                durationMs = 278000,
                artworkUrl = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=600&auto=format&fit=crop&q=80",
                audioResId = R.raw.sample_electronic_bass,
                isFavourite = false,
                genre = "Tamil",
                year = 2022
            ),
            Song(
                id = "tamil_7",
                title = "Rowdy Baby",
                artist = "Yuvan Shankar Raja, Dhanush, Dhee",
                album = "Maari 2",
                durationMs = 284000,
                artworkUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80",
                audioResId = R.raw.sample_tamil_melody,
                isFavourite = true,
                genre = "Tamil",
                year = 2018
            ),
            Song(
                id = "global_1",
                title = "Midnight City Lights",
                artist = "Neon Horizon",
                album = "Cyberpunk Waves",
                durationMs = 215000,
                artworkUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
                audioResId = R.raw.sample_synthwave,
                isFavourite = true,
                genre = "Synthwave",
                year = 2024
            ),
            Song(
                id = "global_2",
                title = "Rainy Café Lo-Fi",
                artist = "Mellow Beats",
                album = "Late Night Study",
                durationMs = 180000,
                artworkUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=600&auto=format&fit=crop&q=80",
                audioResId = R.raw.sample_chill_lofi,
                isFavourite = false,
                genre = "Lo-Fi",
                year = 2024
            ),
            Song(
                id = "global_3",
                title = "Golden Hour Reverie",
                artist = "Aura Bloom",
                album = "Sunburst Solitude",
                durationMs = 230000,
                artworkUrl = "https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=600&auto=format&fit=crop&q=80",
                audioResId = R.raw.sample_acoustic_guitar,
                isFavourite = false,
                genre = "Acoustic",
                year = 2023
            )
        )
    }
}
