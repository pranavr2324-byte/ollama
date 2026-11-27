package com.vibe.music.data.remote

import com.vibe.music.data.model.Artist
import com.vibe.music.data.repository.ArtistRepository
import com.vibe.music.data.repository.MockArtistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteArtistRepository(
    private val apiClient: VibeApiClient = VibeApiClient(),
    private val fallbackRepo: MockArtistRepository = MockArtistRepository()
) : ArtistRepository {

    override fun getPopularArtists(): Flow<List<Artist>> = flow {
        if (VibeNetworkConfig.isRemoteEnabled) {
            when (val result = apiClient.getArtists(limit = 10)) {
                is NetworkResult.Success -> {
                    if (result.data.isNotEmpty()) {
                        emit(result.data)
                        return@flow
                    }
                }
                is NetworkResult.Error -> {}
            }
        }
        fallbackRepo.getPopularArtists().collect { emit(it) }
    }

    override fun getAllArtists(): Flow<List<Artist>> = flow {
        if (VibeNetworkConfig.isRemoteEnabled) {
            when (val result = apiClient.getArtists(limit = 50)) {
                is NetworkResult.Success -> {
                    if (result.data.isNotEmpty()) {
                        emit(result.data)
                        return@flow
                    }
                }
                is NetworkResult.Error -> {}
            }
        }
        fallbackRepo.getAllArtists().collect { emit(it) }
    }

    override suspend fun getArtistById(id: String): Artist? {
        if (VibeNetworkConfig.isRemoteEnabled) {
            when (val result = apiClient.getArtistById(id)) {
                is NetworkResult.Success -> return result.data
                is NetworkResult.Error -> {}
            }
        }
        return fallbackRepo.getArtistById(id)
    }

    override suspend fun searchArtists(query: String): List<Artist> {
        if (VibeNetworkConfig.isRemoteEnabled) {
            when (val result = apiClient.search(query)) {
                is NetworkResult.Success -> {
                    if (result.data.artists.isNotEmpty()) return result.data.artists
                }
                is NetworkResult.Error -> {}
            }
        }
        return fallbackRepo.searchArtists(query)
    }
}
