package com.vibe.music.data.remote

import com.vibe.music.data.model.Playlist
import com.vibe.music.data.model.Song
import com.vibe.music.data.repository.MockPlaylistRepository
import com.vibe.music.data.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemotePlaylistRepository(
    private val apiClient: VibeApiClient = VibeApiClient(),
    private val fallbackRepo: MockPlaylistRepository
) : PlaylistRepository {

    override fun getMoodPlaylists(): Flow<List<Playlist>> = flow {
        fallbackRepo.getMoodPlaylists().collect { emit(it) }
    }

    override fun getUserPlaylists(): Flow<List<Playlist>> = flow {
        if (VibeNetworkConfig.isRemoteEnabled && VibeNetworkConfig.authToken != null) {
            when (val result = apiClient.getPlaylists()) {
                is NetworkResult.Success -> {
                    emit(result.data)
                    return@flow
                }
                is NetworkResult.Error -> {}
            }
        }
        fallbackRepo.getUserPlaylists().collect { emit(it) }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        if (VibeNetworkConfig.isRemoteEnabled && VibeNetworkConfig.authToken != null) {
            when (val result = apiClient.getPlaylists()) {
                is NetworkResult.Success -> {
                    fallbackRepo.getMoodPlaylists().collect { moods ->
                        emit(moods + result.data)
                    }
                    return@flow
                }
                is NetworkResult.Error -> {}
            }
        }
        fallbackRepo.getAllPlaylists().collect { emit(it) }
    }

    override suspend fun getPlaylistById(id: String): Playlist? {
        if (VibeNetworkConfig.isRemoteEnabled) {
            when (val result = apiClient.getPlaylistById(id)) {
                is NetworkResult.Success -> return result.data
                is NetworkResult.Error -> {}
            }
        }
        return fallbackRepo.getPlaylistById(id)
    }

    override suspend fun createPlaylist(title: String, description: String): Playlist {
        if (VibeNetworkConfig.isRemoteEnabled && VibeNetworkConfig.authToken != null) {
            when (val result = apiClient.createPlaylist(title, description)) {
                is NetworkResult.Success -> {
                    fallbackRepo.createPlaylist(title, description)
                    return result.data
                }
                is NetworkResult.Error -> {}
            }
        }
        return fallbackRepo.createPlaylist(title, description)
    }

    override suspend fun addSongToPlaylist(playlistId: String, song: Song): Boolean {
        if (VibeNetworkConfig.isRemoteEnabled && VibeNetworkConfig.authToken != null) {
            apiClient.addSongToPlaylist(playlistId, song.id)
        }
        return fallbackRepo.addSongToPlaylist(playlistId, song)
    }

    override suspend fun removeSongFromPlaylist(playlistId: String, songId: String): Boolean {
        if (VibeNetworkConfig.isRemoteEnabled && VibeNetworkConfig.authToken != null) {
            apiClient.removeSongFromPlaylist(playlistId, songId)
        }
        return fallbackRepo.removeSongFromPlaylist(playlistId, songId)
    }

    override suspend fun deletePlaylist(playlistId: String): Boolean {
        if (VibeNetworkConfig.isRemoteEnabled && VibeNetworkConfig.authToken != null) {
            apiClient.deletePlaylist(playlistId)
        }
        return fallbackRepo.deletePlaylist(playlistId)
    }

    override suspend fun searchPlaylists(query: String): List<Playlist> {
        return fallbackRepo.searchPlaylists(query)
    }
}
