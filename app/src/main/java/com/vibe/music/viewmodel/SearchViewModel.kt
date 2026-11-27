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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SearchFilter(val label: String) {
    ALL("All"),
    SONGS("Songs"),
    ALBUMS("Albums"),
    ARTISTS("Artists"),
    PLAYLISTS("Playlists")
}

class SearchViewModel(
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(SearchFilter.ALL)
    val selectedFilter: StateFlow<SearchFilter> = _selectedFilter.asStateFlow()

    private val _searchHistory = MutableStateFlow(listOf("Anirudh", "Leo", "Hukum", "Lo-Fi Beats", "A.R. Rahman"))
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    private val _songResults = MutableStateFlow<List<Song>>(emptyList())
    val songResults: StateFlow<List<Song>> = _songResults.asStateFlow()

    private val _albumResults = MutableStateFlow<List<Album>>(emptyList())
    val albumResults: StateFlow<List<Album>> = _albumResults.asStateFlow()

    private val _artistResults = MutableStateFlow<List<Artist>>(emptyList())
    val artistResults: StateFlow<List<Artist>> = _artistResults.asStateFlow()

    private val _playlistResults = MutableStateFlow<List<Playlist>>(emptyList())
    val playlistResults: StateFlow<List<Playlist>> = _playlistResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()

        if (query.isBlank()) {
            _songResults.value = emptyList()
            _albumResults.value = emptyList()
            _artistResults.value = emptyList()
            _playlistResults.value = emptyList()
            _isLoading.value = false
            return
        }

        searchJob = viewModelScope.launch {
            _isLoading.value = true
            delay(150) // debounce
            _songResults.value = songRepository.searchSongs(query)
            _albumResults.value = albumRepository.searchAlbums(query)
            _artistResults.value = artistRepository.searchArtists(query)
            _playlistResults.value = playlistRepository.searchPlaylists(query)
            _isLoading.value = false
        }
    }

    fun selectFilter(filter: SearchFilter) {
        _selectedFilter.value = filter
    }

    fun addSearchHistory(query: String) {
        if (query.isNotBlank()) {
            val list = _searchHistory.value.toMutableList()
            list.remove(query)
            list.add(0, query)
            _searchHistory.value = list.take(10)
        }
    }

    fun removeSearchHistory(query: String) {
        _searchHistory.value = _searchHistory.value.filterNot { it == query }
    }

    fun clearSearchHistory() {
        _searchHistory.value = emptyList()
    }
}
