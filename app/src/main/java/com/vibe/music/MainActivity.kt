package com.vibe.music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vibe.music.ui.components.MiniPlayer
import com.vibe.music.ui.navigation.BottomNavigationBar
import com.vibe.music.ui.navigation.Screen
import com.vibe.music.ui.navigation.VibeNavGraph
import com.vibe.music.ui.screens.PlayerScreen
import com.vibe.music.ui.screens.QueueBottomSheet
import com.vibe.music.ui.theme.VibeBackground
import com.vibe.music.ui.theme.VibeTheme
import com.vibe.music.viewmodel.AlbumDetailViewModel
import com.vibe.music.viewmodel.ArtistDetailViewModel
import com.vibe.music.viewmodel.AuthState
import com.vibe.music.viewmodel.AuthViewModel
import com.vibe.music.viewmodel.ExploreViewModel
import com.vibe.music.viewmodel.HomeViewModel
import com.vibe.music.viewmodel.LibraryViewModel
import com.vibe.music.viewmodel.PlayerViewModel
import com.vibe.music.viewmodel.PlaylistDetailViewModel
import com.vibe.music.viewmodel.SearchViewModel
import com.vibe.music.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as VibeApplication
        val songRepo = app.songRepository
        val albumRepo = app.albumRepository
        val artistRepo = app.artistRepository
        val playlistRepo = app.playlistRepository
        val playbackManager = app.playbackManager

        val authViewModel = AuthViewModel()
        val homeViewModel = HomeViewModel(songRepo, albumRepo, artistRepo, playlistRepo)
        val exploreViewModel = ExploreViewModel(songRepo, albumRepo, artistRepo, playlistRepo)
        val searchViewModel = SearchViewModel(songRepo, albumRepo, artistRepo, playlistRepo)
        val libraryViewModel = LibraryViewModel(songRepo, albumRepo, artistRepo, playlistRepo)
        val playerViewModel = PlayerViewModel(playbackManager, songRepo)
        val albumViewModel = AlbumDetailViewModel(albumRepo)
        val artistViewModel = ArtistDetailViewModel(artistRepo)
        val playlistViewModel = PlaylistDetailViewModel(playlistRepo)
        val settingsViewModel = SettingsViewModel()

        setContent {
            VibeTheme {
                VibeApp(
                    authViewModel = authViewModel,
                    homeViewModel = homeViewModel,
                    exploreViewModel = exploreViewModel,
                    searchViewModel = searchViewModel,
                    libraryViewModel = libraryViewModel,
                    playerViewModel = playerViewModel,
                    albumViewModel = albumViewModel,
                    artistViewModel = artistViewModel,
                    playlistViewModel = playlistViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}

@Composable
fun VibeApp(
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    exploreViewModel: ExploreViewModel,
    searchViewModel: SearchViewModel,
    libraryViewModel: LibraryViewModel,
    playerViewModel: PlayerViewModel,
    albumViewModel: AlbumDetailViewModel,
    artistViewModel: ArtistDetailViewModel,
    playlistViewModel: PlaylistDetailViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val authState by authViewModel.authState.collectAsState()
    val playbackState by playerViewModel.playbackState.collectAsState()
    val isFullPlayerExpanded by playerViewModel.isFullPlayerExpanded.collectAsState()
    val isQueueVisible by playerViewModel.isQueueVisible.collectAsState()

    val isTopLevelDestination = Screen.bottomNavItems.any { it.route == currentRoute }
    val isAuthRoute = currentRoute == Screen.LOGIN_ROUTE || currentRoute == Screen.REGISTER_ROUTE

    BackHandler(enabled = isFullPlayerExpanded) {
        playerViewModel.collapsePlayer()
    }

    // React to auth state changes — navigate to/from login
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Unauthenticated -> {
                if (currentRoute != Screen.LOGIN_ROUTE && currentRoute != Screen.REGISTER_ROUTE) {
                    navController.navigate(Screen.LOGIN_ROUTE) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            is AuthState.Authenticated -> {
                if (isAuthRoute || currentRoute == null) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            else -> { /* Loading — stay put */ }
        }
    }

    // Determine start destination based on whether a token exists in prefs
    val startDestination = if (com.vibe.music.data.remote.VibeNetworkConfig.authToken != null) {
        Screen.Home.route
    } else {
        Screen.LOGIN_ROUTE
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VibeBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Main Navigation Graph
        VibeNavGraph(
            navController = navController,
            authViewModel = authViewModel,
            homeViewModel = homeViewModel,
            exploreViewModel = exploreViewModel,
            searchViewModel = searchViewModel,
            libraryViewModel = libraryViewModel,
            playerViewModel = playerViewModel,
            albumViewModel = albumViewModel,
            artistViewModel = artistViewModel,
            playlistViewModel = playlistViewModel,
            settingsViewModel = settingsViewModel,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize()
        )

        // Bottom shell only when not on auth screens
        if (!isAuthRoute) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                // Persistent Mini Player
                if (playbackState.currentSong != null && !isFullPlayerExpanded) {
                    MiniPlayer(
                        playbackState = playbackState,
                        onExpand = { playerViewModel.expandPlayer() },
                        onPlayPause = { playerViewModel.playPause() },
                        onSkipNext = { playerViewModel.skipNext() }
                    )
                }

                // Bottom Navigation Bar (shown on top-level destinations)
                if (isTopLevelDestination) {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onNavigate = { screen ->
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }

        // Full Screen Player Animated Overlay
        AnimatedVisibility(
            visible = isFullPlayerExpanded && playbackState.currentSong != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.fillMaxSize()
        ) {
            PlayerScreen(
                playbackState = playbackState,
                onCollapse = { playerViewModel.collapsePlayer() },
                onPlayPause = { playerViewModel.playPause() },
                onSkipNext = { playerViewModel.skipNext() },
                onSkipPrevious = { playerViewModel.skipPrevious() },
                onSeekToFraction = { playerViewModel.seekToFraction(it) },
                onToggleShuffle = { playerViewModel.toggleShuffle() },
                onToggleRepeat = { playerViewModel.toggleRepeat() },
                onToggleFavourite = { playerViewModel.toggleFavourite(it) },
                onOpenQueue = { playerViewModel.showQueue() },
                onVolumeChange = { playerViewModel.setVolume(it) }
            )
        }

        // Queue Modal Bottom Sheet
        if (isQueueVisible) {
            QueueBottomSheet(
                playbackState = playbackState,
                onDismiss = { playerViewModel.hideQueue() },
                onSongSelected = { playerViewModel.playFromQueue(it) },
                onRemoveSong = { playerViewModel.removeFromQueue(it) },
                onClearQueue = { playerViewModel.clearQueue() },
                onMoveUp = { idx -> playerViewModel.reorderQueue(idx, idx - 1) },
                onMoveDown = { idx -> playerViewModel.reorderQueue(idx, idx + 1) }
            )
        }
    }
}
