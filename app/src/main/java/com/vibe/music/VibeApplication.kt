package com.vibe.music

import android.app.Application
import com.vibe.music.data.remote.RemoteAlbumRepository
import com.vibe.music.data.remote.RemoteArtistRepository
import com.vibe.music.data.remote.RemotePlaylistRepository
import com.vibe.music.data.remote.RemoteSongRepository
import com.vibe.music.data.remote.VibeApiClient
import com.vibe.music.data.remote.VibeNetworkConfig
import com.vibe.music.data.repository.AlbumRepository
import com.vibe.music.data.repository.ArtistRepository
import com.vibe.music.data.repository.MockAlbumRepository
import com.vibe.music.data.repository.MockArtistRepository
import com.vibe.music.data.repository.MockPlaylistRepository
import com.vibe.music.data.repository.MockSongRepository
import com.vibe.music.data.repository.PlaylistRepository
import com.vibe.music.data.repository.SongRepository
import com.vibe.music.player.ExoMusicPlaybackManager
import com.vibe.music.player.MusicPlaybackManager

class VibeApplication : Application() {

    lateinit var songRepository: SongRepository
        private set
    lateinit var albumRepository: AlbumRepository
        private set
    lateinit var artistRepository: ArtistRepository
        private set
    lateinit var playlistRepository: PlaylistRepository
        private set
    lateinit var playbackManager: MusicPlaybackManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        VibeNetworkConfig.init(this)
        val apiClient = VibeApiClient()

        val mockSongRepo = MockSongRepository()
        val mockAlbumRepo = MockAlbumRepository(mockSongRepo)
        val mockArtistRepo = MockArtistRepository()
        val mockPlaylistRepo = MockPlaylistRepository(mockSongRepo)

        val songRepo = RemoteSongRepository(apiClient, mockSongRepo)
        val albumRepo = RemoteAlbumRepository(apiClient, mockAlbumRepo)
        val artistRepo = RemoteArtistRepository(apiClient, mockArtistRepo)
        val playlistRepo = RemotePlaylistRepository(apiClient, mockPlaylistRepo)

        songRepository = songRepo
        albumRepository = albumRepo
        artistRepository = artistRepo
        playlistRepository = playlistRepo

        playbackManager = ExoMusicPlaybackManager(
            context = applicationContext,
            songRepository = songRepo
        )
    }

    override fun onTerminate() {
        super.onTerminate()
        playbackManager.release()
    }

    companion object {
        lateinit var instance: VibeApplication
            private set
    }
}
