package com.vibe.music.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vibe.music.ui.screens.AlbumScreen
import com.vibe.music.ui.screens.ArtistScreen
import com.vibe.music.ui.screens.ExploreScreen
import com.vibe.music.ui.screens.HomeScreen
import com.vibe.music.ui.screens.LibraryScreen
import com.vibe.music.ui.screens.LoginScreen
import com.vibe.music.ui.screens.PlaylistScreen
import com.vibe.music.ui.screens.RegisterScreen
import com.vibe.music.ui.screens.SearchScreen
import com.vibe.music.ui.screens.SettingsScreen
import com.vibe.music.viewmodel.AlbumDetailViewModel
import com.vibe.music.viewmodel.ArtistDetailViewModel
import com.vibe.music.viewmodel.AuthViewModel
import com.vibe.music.viewmodel.ExploreViewModel
import com.vibe.music.viewmodel.HomeViewModel
import com.vibe.music.viewmodel.LibraryViewModel
import com.vibe.music.viewmodel.PlayerViewModel
import com.vibe.music.viewmodel.PlaylistDetailViewModel
import com.vibe.music.viewmodel.SearchViewModel
import com.vibe.music.viewmodel.SettingsViewModel

@Composable
fun VibeNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    exploreViewModel: ExploreViewModel,
    searchViewModel: SearchViewModel,
    libraryViewModel: LibraryViewModel,
    playerViewModel: PlayerViewModel,
    albumViewModel: AlbumDetailViewModel,
    artistViewModel: ArtistDetailViewModel,
    playlistViewModel: PlaylistDetailViewModel,
    settingsViewModel: SettingsViewModel,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.LOGIN_ROUTE) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateRegister = {
                    navController.navigate(Screen.REGISTER_ROUTE) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.REGISTER_ROUTE) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                homeViewModel = homeViewModel,
                playerViewModel = playerViewModel,
                onAlbumClick = { id -> navController.navigate(Screen.albumRoute(id)) },
                onArtistClick = { id -> navController.navigate(Screen.artistRoute(id)) },
                onPlaylistClick = { id -> navController.navigate(Screen.playlistRoute(id)) },
                onNavigateSettings = { navController.navigate(Screen.SETTINGS_ROUTE) }
            )
        }

        composable(Screen.Explore.route) {
            ExploreScreen(
                exploreViewModel = exploreViewModel,
                playerViewModel = playerViewModel,
                onNavigateSearch = { navController.navigate(Screen.Search.route) },
                onAlbumClick = { id -> navController.navigate(Screen.albumRoute(id)) },
                onArtistClick = { id -> navController.navigate(Screen.artistRoute(id)) },
                onPlaylistClick = { id -> navController.navigate(Screen.playlistRoute(id)) }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                searchViewModel = searchViewModel,
                playerViewModel = playerViewModel,
                onAlbumClick = { id -> navController.navigate(Screen.albumRoute(id)) },
                onArtistClick = { id -> navController.navigate(Screen.artistRoute(id)) },
                onPlaylistClick = { id -> navController.navigate(Screen.playlistRoute(id)) }
            )
        }

        composable(Screen.Library.route) {
            LibraryScreen(
                libraryViewModel = libraryViewModel,
                playerViewModel = playerViewModel,
                onAlbumClick = { id -> navController.navigate(Screen.albumRoute(id)) },
                onArtistClick = { id -> navController.navigate(Screen.artistRoute(id)) },
                onPlaylistClick = { id -> navController.navigate(Screen.playlistRoute(id)) }
            )
        }

        composable(
            route = Screen.ALBUM_ROUTE,
            arguments = listOf(navArgument("albumId") { type = NavType.StringType })
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getString("albumId") ?: ""
            AlbumScreen(
                albumId = albumId,
                viewModel = albumViewModel,
                playerViewModel = playerViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ARTIST_ROUTE,
            arguments = listOf(navArgument("artistId") { type = NavType.StringType })
        ) { backStackEntry ->
            val artistId = backStackEntry.arguments?.getString("artistId") ?: ""
            ArtistScreen(
                artistId = artistId,
                viewModel = artistViewModel,
                playerViewModel = playerViewModel,
                onAlbumClick = { id -> navController.navigate(Screen.albumRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PLAYLIST_ROUTE,
            arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getString("playlistId") ?: ""
            PlaylistScreen(
                playlistId = playlistId,
                viewModel = playlistViewModel,
                playerViewModel = playerViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SETTINGS_ROUTE) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                }
            )
        }
    }
}
