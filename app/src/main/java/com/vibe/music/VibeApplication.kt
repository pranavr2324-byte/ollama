package com.vibe.music

import android.app.Application
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

        val songRepo = MockSongRepository()
        val albumRepo = MockAlbumRepository(songRepo)
        val artistRepo = MockArtistRepository()
        val playlistRepo = MockPlaylistRepository(songRepo)

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
