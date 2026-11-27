package com.vibe.music.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibe.music.data.model.PlaybackState
import com.vibe.music.data.model.Song
import com.vibe.music.data.repository.SongRepository
import com.vibe.music.player.MusicPlaybackManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playbackManager: MusicPlaybackManager,
    private val songRepository: SongRepository
) : ViewModel() {

    val playbackState: StateFlow<PlaybackState> = playbackManager.playbackState

    private val _isFullPlayerExpanded = MutableStateFlow(false)
    val isFullPlayerExpanded: StateFlow<Boolean> = _isFullPlayerExpanded.asStateFlow()

    private val _isQueueVisible = MutableStateFlow(false)
    val isQueueVisible: StateFlow<Boolean> = _isQueueVisible.asStateFlow()

    fun expandPlayer() {
        _isFullPlayerExpanded.value = true
    }

    fun collapsePlayer() {
        _isFullPlayerExpanded.value = false
        _isQueueVisible.value = false
    }

    fun showQueue() {
        _isQueueVisible.value = true
    }

    fun hideQueue() {
        _isQueueVisible.value = false
    }

    fun playSong(song: Song, queue: List<Song> = listOf(song)) {
        playbackManager.playSong(song, queue)
    }

    fun playQueue(queue: List<Song>, startIndex: Int = 0) {
        playbackManager.playQueue(queue, startIndex)
    }

    fun playPause() {
        playbackManager.playPause()
    }

    fun skipNext() {
        playbackManager.skipToNext()
    }

    fun skipPrevious() {
        playbackManager.skipToPrevious()
    }

    fun seekTo(positionMs: Long) {
        playbackManager.seekTo(positionMs)
    }

    fun seekToFraction(fraction: Float) {
        val duration = playbackState.value.durationMs
        if (duration > 0) {
            val targetMs = (fraction * duration).toLong()
            playbackManager.seekTo(targetMs)
        }
    }

    fun toggleShuffle() {
        playbackManager.toggleShuffle()
    }

    fun toggleRepeat() {
        playbackManager.toggleRepeatMode()
    }

    fun setVolume(volume: Float) {
        playbackManager.setVolume(volume)
    }

    fun toggleFavourite(songId: String) {
        viewModelScope.launch {
            songRepository.toggleFavourite(songId)
            // If current song is the toggled one, update its state
            val current = playbackState.value.currentSong
            if (current != null && current.id == songId) {
                // state will update from repository listeners or copy
            }
        }
    }

    fun playFromQueue(index: Int) {
        playbackManager.playFromQueue(index)
    }

    fun removeFromQueue(index: Int) {
        playbackManager.removeFromQueue(index)
    }

    fun clearQueue() {
        playbackManager.clearQueue()
    }

    fun reorderQueue(fromIndex: Int, toIndex: Int) {
        playbackManager.reorderQueue(fromIndex, toIndex)
    }
}
