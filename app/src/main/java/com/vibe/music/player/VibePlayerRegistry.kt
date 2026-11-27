package com.vibe.music.player

import androidx.media3.exoplayer.ExoPlayer

/**
 * Singleton registry that lets [VibeMusicService] access the same ExoPlayer
 * instance created by [ExoMusicPlaybackManager], without introducing a hard
 * dependency or leaking the Context.
 */
object VibePlayerRegistry {
    @Volatile
    var player: ExoPlayer? = null
}
