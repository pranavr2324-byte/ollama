package com.vibe.music.player

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.vibe.music.MainActivity

/**
 * VibeMusicService — foreground MediaSessionService that keeps ExoPlayer alive
 * when the app is in the background and powers lock-screen / notification controls.
 *
 * The ExoPlayer instance is shared from [ExoMusicPlaybackManager] via [VibePlayerRegistry].
 * If no player is registered yet (app cold-started via media notification), we create a
 * standalone player for the service.
 */
class VibeMusicService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private var ownsPlayer: Boolean = false

    override fun onCreate() {
        super.onCreate()

        // Obtain the shared player from the registry (set by ExoMusicPlaybackManager)
        val sharedPlayer = VibePlayerRegistry.player
        val player = if (sharedPlayer != null) {
            ownsPlayer = false
            sharedPlayer
        } else {
            ownsPlayer = true
            ExoPlayer.Builder(this)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .build(),
                    /* handleAudioFocus= */ true
                )
                .setHandleAudioBecomingNoisy(true)
                .build()
        }

        val activityIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(activityIntent)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            if (ownsPlayer) {
                player.release()
            }
            release()
        }
        mediaSession = null
        super.onDestroy()
    }
}
