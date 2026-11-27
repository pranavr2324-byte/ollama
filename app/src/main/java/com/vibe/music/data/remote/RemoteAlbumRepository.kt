package com.vibe.music.data.remote

import com.vibe.music.data.model.Album
import com.vibe.music.data.repository.AlbumRepository
import com.vibe.music.data.repository.MockAlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteAlbumRepository(
    private val apiClient: VibeApiClient = VibeApiClient(),
    private val fallbackRepo: MockAlbumRepository
) : AlbumRepository {

    override fun getRecommendedAlbums(): Flow<List<Album>> = flow {
        if (VibeNetworkConfig.isRemoteEnabled) {
            when (val result = apiClient.getAlbums(limit = 10)) {
                is NetworkResult.Success -> {
                    if (result.data.isNotEmpty()) {
                        emit(result.data)
                        return@flow
                    }
                }
                is NetworkResult.Error -> {}
            }
        }
        fallbackRepo.getRecommendedAlbums().collect { emit(it) }
    }

    override fun getAllAlbums(): Flow<List<Album>> = flow {
        if (VibeNetworkConfig.isRemoteEnabled) {
            when (val result = apiClient.getAlbums(limit = 50)) {
                is NetworkResult.Success -> {
                    if (result.data.isNotEmpty()) {
                        emit(result.data)
                        return@flow
                    }
                }
                is NetworkResult.Error -> {}
            }
        }
        fallbackRepo.getAllAlbums().collect { emit(it) }
    }

    override suspend fun getAlbumById(id: String): Album? {
        if (VibeNetworkConfig.isRemoteEnabled) {
            when (val result = apiClient.getAlbumById(id)) {
                is NetworkResult.Success -> return result.data
                is NetworkResult.Error -> {}
            }
        }
        return fallbackRepo.getAlbumById(id)
    }

    override suspend fun searchAlbums(query: String): List<Album> {
        if (VibeNetworkConfig.isRemoteEnabled) {
            when (val result = apiClient.search(query)) {
                is NetworkResult.Success -> {
                    if (result.data.albums.isNotEmpty()) return result.data.albums
                }
                is NetworkResult.Error -> {}
            }
        }
        return fallbackRepo.searchAlbums(query)
    }
}
