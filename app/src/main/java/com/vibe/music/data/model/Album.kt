package com.vibe.music.data.model

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val artworkUrl: String,
    val year: Int,
    val songCount: Int,
    val songs: List<Song> = emptyList(),
    val genre: String = "Various"
)
