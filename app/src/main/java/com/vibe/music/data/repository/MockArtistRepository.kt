package com.vibe.music.data.repository

import com.vibe.music.data.model.Artist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockArtistRepository : ArtistRepository {

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())

    init {
        val allSongs = MockSongRepository.initialSongs()
        _artists.value = listOf(
            Artist(
                id = "artist_anirudh",
                name = "Anirudh Ravichander",
                imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
                monthlyListeners = "18.4M Monthly Listeners",
                bio = "Rockstar Anirudh is one of India's leading music composers, known for iconic chartbusters across Tamil, Telugu, and Hindi cinema.",
                popularSongs = allSongs.filter { it.artist.contains("Anirudh") }
            ),
            Artist(
                id = "artist_ar_rahman",
                name = "A.R. Rahman",
                imageUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
                monthlyListeners = "24.1M Monthly Listeners",
                bio = "Two-time Academy Award winner and global music maestro, revolutionizing Indian contemporary and film music for over three decades.",
                popularSongs = allSongs.filter { it.artist.contains("A.R. Rahman") }
            ),
            Artist(
                id = "artist_yuvan",
                name = "Yuvan Shankar Raja",
                imageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
                monthlyListeners = "12.8M Monthly Listeners",
                bio = "Youth icon and maestro known as U1, pioneering hip-hop and electronic fusion into Tamil mainstream music.",
                popularSongs = allSongs.filter { it.artist.contains("Yuvan") }
            ),
            Artist(
                id = "artist_sai",
                name = "Sai Abhyankkar",
                imageUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=600&auto=format&fit=crop&q=80",
                monthlyListeners = "7.2M Monthly Listeners",
                bio = "Viral sensational singer and producer with breakout global indie hits 'Katchi Sera' and 'Aasa Kooda'.",
                popularSongs = allSongs.filter { it.artist.contains("Sai Abhyankkar") }
            ),
            Artist(
                id = "artist_neon",
                name = "Neon Horizon",
                imageUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
                monthlyListeners = "5.6M Monthly Listeners",
                bio = "Electronic producer pushing the boundaries of retro synthwave and atmospheric cyberpunk soundscapes.",
                popularSongs = allSongs.filter { it.artist.contains("Neon") }
            )
        )
    }

    override fun getPopularArtists(): Flow<List<Artist>> = _artists.asStateFlow()

    override fun getAllArtists(): Flow<List<Artist>> = _artists.asStateFlow()

    override suspend fun getArtistById(id: String): Artist? {
        return _artists.value.find { it.id == id }
    }

    override suspend fun searchArtists(query: String): List<Artist> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        return _artists.value.filter { it.name.lowercase().contains(q) }
    }
}
