package com.vibe.music.data.model

data class Artist(
    val id: String,
    val name: String,
    val imageUrl: String,
    val monthlyListeners: String,
    val bio: String = "",
    val popularSongs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList()
)
