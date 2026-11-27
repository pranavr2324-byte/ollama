package com.vibe.music.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibe.music.data.model.Album
import com.vibe.music.data.model.Artist
import com.vibe.music.data.model.Playlist
import com.vibe.music.data.model.Song
import com.vibe.music.data.repository.AlbumRepository
import com.vibe.music.data.repository.ArtistRepository
import com.vibe.music.data.repository.PlaylistRepository
import com.vibe.music.data.repository.SongRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    val greeting: String
        get() {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            return when (hour) {
                in 4..11 -> "Good morning"
                in 12..16 -> "Good afternoon"
                in 17..21 -> "Good evening"
                else -> "Late night vibe"
            }
        }

    val featuredSongs: StateFlow<List<Song>> = songRepository.getFeaturedSongs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trendingTamilSongs: StateFlow<List<Song>> = songRepository.getTrendingTamilSongs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyPlayed: StateFlow<List<Song>> = songRepository.getRecentlyPlayed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recommendedAlbums: StateFlow<List<Album>> = albumRepository.getRecommendedAlbums()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val popularArtists: StateFlow<List<Artist>> = artistRepository.getPopularArtists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val moodPlaylists: StateFlow<List<Playlist>> = playlistRepository.getMoodPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavourite(songId: String) {
        viewModelScope.launch {
            songRepository.toggleFavourite(songId)
        }
    }
}
