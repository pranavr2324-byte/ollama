package com.vibe.music.data.model

data class Playlist(
    val id: String,
    val title: String,
    val description: String = "",
    val artworkUrl: String,
    val songCount: Int = 0,
    val songs: List<Song> = emptyList(),
    val isUserCreated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
