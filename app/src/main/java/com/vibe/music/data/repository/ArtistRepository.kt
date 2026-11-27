package com.vibe.music.data.repository

import com.vibe.music.data.model.Artist
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
    fun getPopularArtists(): Flow<List<Artist>>
    fun getAllArtists(): Flow<List<Artist>>
    suspend fun getArtistById(id: String): Artist?
    suspend fun searchArtists(query: String): List<Artist>
}
