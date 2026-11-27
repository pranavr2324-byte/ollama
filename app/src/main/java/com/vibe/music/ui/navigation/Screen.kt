package com.vibe.music.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Explore : Screen("explore", "Explore", Icons.Filled.Explore, Icons.Outlined.Explore)
    object Search : Screen("search", "Search", Icons.Filled.Search, Icons.Outlined.Search)
    object Library : Screen("library", "Library", Icons.Filled.LibraryMusic, Icons.Outlined.LibraryMusic)

    companion object {
        val bottomNavItems = listOf(Home, Explore, Search, Library)

        const val ALBUM_ROUTE = "album/{albumId}"
        fun albumRoute(albumId: String) = "album/$albumId"

        const val ARTIST_ROUTE = "artist/{artistId}"
        fun artistRoute(artistId: String) = "artist/$artistId"

        const val PLAYLIST_ROUTE = "playlist/{playlistId}"
        fun playlistRoute(playlistId: String) = "playlist/$playlistId"

        const val SETTINGS_ROUTE = "settings"
    }
}
