package com.vibe.music.data.repository

import com.vibe.music.data.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun getAllSongs(): Flow<List<Song>>
    fun getFeaturedSongs(): Flow<List<Song>>
    fun getTrendingTamilSongs(): Flow<List<Song>>
    fun getRecentlyPlayed(): Flow<List<Song>>
    fun getFavouriteSongs(): Flow<List<Song>>
    suspend fun getSongById(id: String): Song?
    suspend fun toggleFavourite(songId: String): Boolean
    suspend fun recordPlay(songId: String)
    suspend fun searchSongs(query: String): List<Song>
}
