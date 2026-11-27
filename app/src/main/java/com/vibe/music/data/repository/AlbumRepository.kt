package com.vibe.music.data.repository

import com.vibe.music.data.model.Album
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    fun getRecommendedAlbums(): Flow<List<Album>>
    fun getAllAlbums(): Flow<List<Album>>
    suspend fun getAlbumById(id: String): Album?
    suspend fun searchAlbums(query: String): List<Album>
}
