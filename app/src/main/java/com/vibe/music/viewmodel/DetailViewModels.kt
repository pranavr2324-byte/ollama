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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlbumDetailViewModel(
    private val albumRepository: AlbumRepository
) : ViewModel() {
    private val _album = MutableStateFlow<Album?>(null)
    val album: StateFlow<Album?> = _album.asStateFlow()

    fun loadAlbum(albumId: String) {
        viewModelScope.launch {
            _album.value = albumRepository.getAlbumById(albumId)
        }
    }
}

class ArtistDetailViewModel(
    private val artistRepository: ArtistRepository
) : ViewModel() {
    private val _artist = MutableStateFlow<Artist?>(null)
    val artist: StateFlow<Artist?> = _artist.asStateFlow()

    fun loadArtist(artistId: String) {
        viewModelScope.launch {
            _artist.value = artistRepository.getArtistById(artistId)
        }
    }
}

class PlaylistDetailViewModel(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {
    private val _playlist = MutableStateFlow<Playlist?>(null)
    val playlist: StateFlow<Playlist?> = _playlist.asStateFlow()

    fun loadPlaylist(playlistId: String) {
        viewModelScope.launch {
            _playlist.value = playlistRepository.getPlaylistById(playlistId)
        }
    }

    fun removeSong(songId: String) {
        val pl = _playlist.value ?: return
        viewModelScope.launch {
            playlistRepository.removeSongFromPlaylist(pl.id, songId)
            _playlist.value = playlistRepository.getPlaylistById(pl.id)
        }
    }

    fun addSong(song: Song) {
        val pl = _playlist.value ?: return
        viewModelScope.launch {
            playlistRepository.addSongToPlaylist(pl.id, song)
            _playlist.value = playlistRepository.getPlaylistById(pl.id)
        }
    }
}
