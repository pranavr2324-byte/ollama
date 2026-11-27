package com.vibe.music.data.model

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val artworkUrl: String,
    val audioResId: Int? = null,
    val streamUrl: String? = null,
    val isFavourite: Boolean = false,
    val playCount: Long = 0,
    val genre: String = "Various",
    val year: Int = 2024
) {
    val durationFormatted: String
        get() {
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
}
