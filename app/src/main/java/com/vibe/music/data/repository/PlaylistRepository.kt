package com.vibe.music.data.repository

import com.vibe.music.data.model.Playlist
import com.vibe.music.data.model.Song
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getMoodPlaylists(): Flow<List<Playlist>>
    fun getUserPlaylists(): Flow<List<Playlist>>
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(id: String): Playlist?
    suspend fun createPlaylist(title: String, description: String = ""): Playlist
    suspend fun addSongToPlaylist(playlistId: String, song: Song): Boolean
    suspend fun removeSongFromPlaylist(playlistId: String, songId: String): Boolean
    suspend fun deletePlaylist(playlistId: String): Boolean
    suspend fun searchPlaylists(query: String): List<Playlist>
}
