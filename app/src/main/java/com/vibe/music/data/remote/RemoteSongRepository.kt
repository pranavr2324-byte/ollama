package com.vibe.music.data.remote

import com.vibe.music.data.model.Song
import com.vibe.music.data.repository.MockSongRepository
import com.vibe.music.data.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteSongRepository(
    private val apiClient: VibeApiClient = VibeApiClient(),
    private val fallbackRepo: MockSongRepository = MockSongRepository()
) : SongRepository {

    override fun getAllSongs(): Flow<List<Song>> = flow {
        if (VibeNetworkConfig.isRemoteEnabled) {
            when (val result = apiClient.getSongs(limit = 50)) {
                is NetworkResult.Success -> {
                    if (result.data.isNotEmpty()) {
                        emit(result.data)
                        return@flow
                    }
                }
                is NetworkResult.Error -> {
                    // Fall back to bundled offline store
                }
            }
        }
        fallbackRepo.getAllSongs().collect { emit(it) }
    }

    override fun getFeaturedSongs(): Flow<List<Song>> = flow {
        if (VibeNetworkConfig.isRemoteEnabled) {
            when (val result = apiClient.getSongs(limit = 10)) {
                is NetworkResult.Success -> {
                    if (result.data.isNotEmpty()) {
                        emit(result.data)
                        return@flow
                    }
                }
                is NetworkResult.Error -> {}
            }
        }
        fallbackRepo.getFeaturedSongs().collect { emit(it) }
    }

    override fun getTrendingTamilSongs(): Flow<List<Song>> = flow {
        if (VibeNetworkConfig.isRemoteEnabled) {
            when (val result = apiClient.getSongs(genre = "Tamil", limit = 10)) {
                is NetworkResult.Success -> {
                    if (result.data.isNotEmpty()) {
                        emit(result.data)
                        return@flow
                    }
                }
                is NetworkResult.Error -> {}
            }
        }
        fallbackRepo.getTrendingTamilSongs().collect { emit(it) }
    }

    override fun getRecentlyPlayed(): Flow<List<Song>> = flow {
        if (VibeNetworkConfig.isRemoteEnabled && VibeNetworkConfig.authToken != null) {
            when (val result = apiClient.getRecentlyPlayed()) {
                is NetworkResult.Success -> {
                    if (result.data.isNotEmpty()) {
                        emit(result.data)
                        return@flow
                    }
                }
                is NetworkResult.Error -> {}
            }
        }
        fallbackRepo.getRecentlyPlayed().collect { emit(it) }
    }

    override fun getFavouriteSongs(): Flow<List<Song>> = flow {
        if (VibeNetworkConfig.isRemoteEnabled && VibeNetworkConfig.authToken != null) {
            when (val result = apiClient.getFavourites()) {
                is NetworkResult.Success -> {
                    emit(result.data)
                    return@flow
                }
                is NetworkResult.Error -> {}
            }
        }
        fallbackRepo.getFavouriteSongs().collect { emit(it) }
    }

    override suspend fun getSongById(id: String): Song? {
        if (VibeNetworkConfig.isRemoteEnabled) {
            when (val result = apiClient.getSongById(id)) {
                is NetworkResult.Success -> return result.data
                is NetworkResult.Error -> {}
            }
        }
        return fallbackRepo.getSongById(id)
    }

    override suspend fun toggleFavourite(songId: String): Boolean {
        if (VibeNetworkConfig.isRemoteEnabled && VibeNetworkConfig.authToken != null) {
            val current = getSongById(songId)?.isFavourite ?: false
            when (val result = apiClient.toggleFavourite(songId, current)) {
                is NetworkResult.Success -> return result.data
                is NetworkResult.Error -> {}
            }
        }
        return fallbackRepo.toggleFavourite(songId)
    }

    override suspend fun recordPlay(songId: String) {
        if (VibeNetworkConfig.isRemoteEnabled && VibeNetworkConfig.authToken != null) {
            apiClient.recordRecentlyPlayed(songId)
        }
        fallbackRepo.recordPlay(songId)
    }

    override suspend fun searchSongs(query: String): List<Song> {
        if (VibeNetworkConfig.isRemoteEnabled) {
            when (val result = apiClient.search(query)) {
                is NetworkResult.Success -> {
                    if (result.data.songs.isNotEmpty()) return result.data.songs
                }
                is NetworkResult.Error -> {}
            }
        }
        return fallbackRepo.searchSongs(query)
    }
}
