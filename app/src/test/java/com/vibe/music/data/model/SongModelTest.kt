package com.vibe.music.data.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SongModelTest {

    @Test
    fun testSongDurationFormatting() {
        val song1 = Song(
            id = "1",
            title = "Chill Lofi",
            artist = "Vibe Lab",
            album = "Midnight Sessions",
            durationMs = 185000L, // 3 mins 5 secs
            artworkUrl = "https://example.com/art.jpg"
        )

        assertEquals("3:05", song1.durationFormatted)

        val song2 = Song(
            id = "2",
            title = "Tamil Melody",
            artist = "Anirudh",
            album = "Hits",
            durationMs = 60000L, // 1 min 0 secs
            artworkUrl = "https://example.com/art.jpg"
        )

        assertEquals("1:00", song2.durationFormatted)
    }

    @Test
    fun testPlaybackStateDefaults() {
        val state = PlaybackState()

        assertFalse(state.isPlaying)
        assertFalse(state.isBuffering)
        assertEquals(0L, state.currentPositionMs)
        assertEquals(RepeatMode.OFF, state.repeatMode)
        assertFalse(state.isShuffleEnabled)
        assertEquals(1.0f, state.volume, 0.001f)
    }

    @Test
    fun testRepeatModeCycle() {
        val mode = RepeatMode.OFF
        val all = RepeatMode.ALL
        val one = RepeatMode.ONE

        assertTrue(mode != all)
        assertTrue(all != one)
    }
}
