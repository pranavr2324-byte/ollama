package com.vibe.music.player

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.vibe.music.data.model.PlaybackState
import com.vibe.music.data.model.RepeatMode
import com.vibe.music.data.model.Song
import com.vibe.music.data.repository.SongRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
class ExoMusicPlaybackManager(
    private val context: Context,
    private val songRepository: SongRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())
) : MusicPlaybackManager {

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    private val _playbackState = MutableStateFlow(PlaybackState())
    override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var progressJob: Job? = null
    private var internalQueue: MutableList<Song> = mutableListOf()

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val isBuffering = playbackState == Player.STATE_BUFFERING
                _playbackState.value = _playbackState.value.copy(
                    isBuffering = isBuffering,
                    durationMs = exoPlayer.duration.coerceAtLeast(0L)
                )

                if (playbackState == Player.STATE_ENDED) {
                    when (_playbackState.value.repeatMode) {
                        RepeatMode.ONE -> {
                            exoPlayer.seekTo(0)
                            exoPlayer.play()
                        }
                        RepeatMode.ALL -> {
                            skipToNext()
                        }
                        RepeatMode.OFF -> {
                            if (_playbackState.value.currentQueueIndex < internalQueue.size - 1) {
                                skipToNext()
                            } else {
                                pause()
                                exoPlayer.seekTo(0)
                            }
                        }
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playbackState.value = _playbackState.value.copy(isPlaying = isPlaying)
                if (isPlaying) {
                    startProgressTracker()
                } else {
                    stopProgressTracker()
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val currentIdx = exoPlayer.currentMediaItemIndex
                if (currentIdx in internalQueue.indices) {
                    val song = internalQueue[currentIdx]
                    _playbackState.value = _playbackState.value.copy(
                        currentSong = song,
                        currentQueueIndex = currentIdx,
                        durationMs = song.durationMs
                    )
                    scope.launch {
                        songRepository.recordPlay(song.id)
                    }
                }
            }
        })
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                val currentPos = exoPlayer.currentPosition.coerceAtLeast(0L)
                val duration = if (exoPlayer.duration > 0) exoPlayer.duration else _playbackState.value.currentSong?.durationMs ?: 0L
                _playbackState.value = _playbackState.value.copy(
                    currentPositionMs = currentPos,
                    durationMs = duration
                )
                delay(300)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    override fun playSong(song: Song, queue: List<Song>) {
        val playList = if (queue.contains(song)) queue else listOf(song) + queue
        val startIndex = playList.indexOf(song).coerceAtLeast(0)
        playQueue(playList, startIndex)
    }

    override fun playQueue(queue: List<Song>, startIndex: Int) {
        if (queue.isEmpty()) return
        internalQueue.clear()
        internalQueue.addAll(queue)

        exoPlayer.stop()
        exoPlayer.clearMediaItems()

        val mediaItems = queue.map { song ->
            val uri = when {
                song.audioResId != null -> Uri.parse("android.resource://${context.packageName}/${song.audioResId}")
                song.streamUrl != null -> Uri.parse(song.streamUrl)
                else -> Uri.EMPTY
            }

            MediaItem.Builder()
                .setMediaId(song.id)
                .setUri(uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .setAlbumTitle(song.album)
                        .setArtworkUri(Uri.parse(song.artworkUrl))
                        .build()
                )
                .build()
        }

        exoPlayer.setMediaItems(mediaItems, startIndex, 0L)
        exoPlayer.prepare()
        exoPlayer.play()

        val currentSong = queue.getOrNull(startIndex)
        _playbackState.value = _playbackState.value.copy(
            currentSong = currentSong,
            queue = queue,
            currentQueueIndex = startIndex,
            durationMs = currentSong?.durationMs ?: 0L,
            currentPositionMs = 0L,
            isPlaying = true
        )
    }

    override fun pause() {
        exoPlayer.pause()
        _playbackState.value = _playbackState.value.copy(isPlaying = false)
    }

    override fun resume() {
        exoPlayer.play()
        _playbackState.value = _playbackState.value.copy(isPlaying = true)
    }

    override fun playPause() {
        if (exoPlayer.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    override fun skipToNext() {
        if (internalQueue.isEmpty()) return
        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNextMediaItem()
        } else if (_playbackState.value.repeatMode == RepeatMode.ALL) {
            exoPlayer.seekTo(0, 0L)
        }
    }

    override fun skipToPrevious() {
        if (exoPlayer.currentPosition > 3000L) {
            exoPlayer.seekTo(0L)
        } else if (exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.seekToPreviousMediaItem()
        } else {
            exoPlayer.seekTo(0L)
        }
    }

    override fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
        _playbackState.value = _playbackState.value.copy(currentPositionMs = positionMs)
    }

    override fun toggleShuffle() {
        val newShuffle = !_playbackState.value.isShuffleEnabled
        exoPlayer.shuffleModeEnabled = newShuffle
        _playbackState.value = _playbackState.value.copy(isShuffleEnabled = newShuffle)
    }

    override fun toggleRepeatMode() {
        val nextMode = when (_playbackState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        val exoMode = when (nextMode) {
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
        }
        exoPlayer.repeatMode = exoMode
        _playbackState.value = _playbackState.value.copy(repeatMode = nextMode)
    }

    override fun setVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        exoPlayer.volume = clamped
        _playbackState.value = _playbackState.value.copy(volume = clamped)
    }

    override fun playFromQueue(index: Int) {
        if (index in internalQueue.indices) {
            exoPlayer.seekTo(index, 0L)
            exoPlayer.play()
        }
    }

    override fun removeFromQueue(index: Int) {
        if (index in internalQueue.indices) {
            internalQueue.removeAt(index)
            exoPlayer.removeMediaItem(index)
            _playbackState.value = _playbackState.value.copy(
                queue = internalQueue.toList(),
                currentQueueIndex = exoPlayer.currentMediaItemIndex
            )
        }
    }

    override fun clearQueue() {
        internalQueue.clear()
        exoPlayer.clearMediaItems()
        _playbackState.value = PlaybackState()
    }

    override fun reorderQueue(fromIndex: Int, toIndex: Int) {
        if (fromIndex in internalQueue.indices && toIndex in internalQueue.indices) {
            val item = internalQueue.removeAt(fromIndex)
            internalQueue.add(toIndex, item)
            exoPlayer.moveMediaItem(fromIndex, toIndex)
            _playbackState.value = _playbackState.value.copy(
                queue = internalQueue.toList(),
                currentQueueIndex = exoPlayer.currentMediaItemIndex
            )
        }
    }

    override fun release() {
        stopProgressTracker()
        exoPlayer.release()
    }
}
