package com.vibe.music.player

import com.vibe.music.data.model.PlaybackState
import com.vibe.music.data.model.RepeatMode
import com.vibe.music.data.model.Song
import kotlinx.coroutines.flow.StateFlow

interface MusicPlaybackManager {
    val playbackState: StateFlow<PlaybackState>

    fun playSong(song: Song, queue: List<Song> = listOf(song))
    fun playQueue(queue: List<Song>, startIndex: Int = 0)
    fun pause()
    fun resume()
    fun playPause()
    fun skipToNext()
    fun skipToPrevious()
    fun seekTo(positionMs: Long)
    fun toggleShuffle()
    fun toggleRepeatMode()
    fun setVolume(volume: Float)
    fun playFromQueue(index: Int)
    fun removeFromQueue(index: Int)
    fun clearQueue()
    fun reorderQueue(fromIndex: Int, toIndex: Int)
    fun release()
}
