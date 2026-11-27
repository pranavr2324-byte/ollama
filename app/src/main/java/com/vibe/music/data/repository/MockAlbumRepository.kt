package com.vibe.music.data.repository

import com.vibe.music.data.model.Album
import com.vibe.music.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockAlbumRepository(
    private val songRepository: SongRepository
) : AlbumRepository {

    private val _albums = MutableStateFlow<List<Album>>(emptyList())

    init {
        val allSongs = MockSongRepository.initialSongs()
        _albums.value = listOf(
            Album(
                id = "album_leo",
                title = "Leo",
                artist = "Anirudh Ravichander",
                artworkUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
                year = 2023,
                songCount = 6,
                songs = allSongs.filter { it.album == "Leo" },
                genre = "Tamil Soundtrack"
            ),
            Album(
                id = "album_jailer",
                title = "Jailer",
                artist = "Anirudh Ravichander",
                artworkUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
                year = 2023,
                songCount = 8,
                songs = allSongs.filter { it.album == "Jailer" },
                genre = "Mass & EDM"
            ),
            Album(
                id = "album_cyberpunk",
                title = "Cyberpunk Waves",
                artist = "Neon Horizon",
                artworkUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
                year = 2024,
                songCount = 10,
                songs = allSongs.filter { it.album == "Cyberpunk Waves" },
                genre = "Synthwave"
            ),
            Album(
                id = "album_lofi",
                title = "Late Night Study",
                artist = "Mellow Beats",
                artworkUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=600&auto=format&fit=crop&q=80",
                year = 2024,
                songCount = 12,
                songs = allSongs.filter { it.album == "Late Night Study" },
                genre = "Lo-Fi Beats"
            ),
            Album(
                id = "album_vtkk",
                title = "Vendhu Thanindhathu Kaadu",
                artist = "A.R. Rahman",
                artworkUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
                year = 2022,
                songCount = 5,
                songs = allSongs.filter { it.album == "Vendhu Thanindhathu Kaadu" },
                genre = "Tamil Melody"
            )
        )
    }

    override fun getRecommendedAlbums(): Flow<List<Album>> = _albums.asStateFlow()

    override fun getAllAlbums(): Flow<List<Album>> = _albums.asStateFlow()

    override suspend fun getAlbumById(id: String): Album? {
        return _albums.value.find { it.id == id }
    }

    override suspend fun searchAlbums(query: String): List<Album> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        return _albums.value.filter {
            it.title.lowercase().contains(q) || it.artist.lowercase().contains(q)
        }
    }
}
