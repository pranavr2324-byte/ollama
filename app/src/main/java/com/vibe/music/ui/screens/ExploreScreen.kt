package com.vibe.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibe.music.data.model.Genre
import com.vibe.music.ui.components.AlbumCard
import com.vibe.music.ui.components.ArtistAvatarItem
import com.vibe.music.ui.components.PlaylistCard
import com.vibe.music.ui.components.SectionHeader
import com.vibe.music.ui.components.SongListItem
import com.vibe.music.ui.theme.VibeSecondary
import com.vibe.music.ui.theme.VibeSurface
import com.vibe.music.ui.theme.VibeSurfaceElevated
import com.vibe.music.ui.theme.VibeSurfaceGlass
import com.vibe.music.ui.theme.VibeTextMuted
import com.vibe.music.ui.theme.VibeTextPrimary
import com.vibe.music.ui.theme.VibeTextSecondary
import com.vibe.music.viewmodel.ExploreViewModel
import com.vibe.music.viewmodel.PlayerViewModel

@Composable
fun ExploreScreen(
    exploreViewModel: ExploreViewModel,
    playerViewModel: PlayerViewModel,
    onNavigateSearch: () -> Unit,
    onAlbumClick: (String) -> Unit,
    onArtistClick: (String) -> Unit,
    onPlaylistClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCategory by exploreViewModel.selectedCategory.collectAsState()
    val trendingSongs by exploreViewModel.trendingSongs.collectAsState()
    val newReleases by exploreViewModel.newReleases.collectAsState()
    val featuredArtists by exploreViewModel.featuredArtists.collectAsState()
    val recommendedPlaylists by exploreViewModel.recommendedPlaylists.collectAsState()
    val playbackState by playerViewModel.playbackState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // Explore Header
        item {
            Text(
                text = "Explore & Discover",
                color = VibeTextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }

        // Quick Search Action Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(VibeSurfaceElevated)
                    .clickable { onNavigateSearch() }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = VibeSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Songs, albums, artists, or genres...",
                        color = VibeTextMuted,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(exploreViewModel.categories) { category ->
                    val isSelected = category == selectedCategory
                    AssistChip(
                        onClick = { exploreViewModel.selectCategory(category) },
                        label = {
                            Text(
                                text = category,
                                color = if (isSelected) Color.White else VibeTextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (isSelected) VibeSecondary else VibeSurfaceElevated
                        ),
                        border = null,
                        shape = CircleShape
                    )
                }
            }
        }

        // New Releases Section
        item {
            Spacer(modifier = Modifier.height(12.dp))
            SectionHeader(title = "New Releases & Spotlights")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(newReleases) { album ->
                    AlbumCard(
                        album = album,
                        onClick = { onAlbumClick(album.id) },
                        onPlayClick = {
                            if (album.songs.isNotEmpty()) {
                                playerViewModel.playQueue(album.songs, 0)
                            }
                        }
                    )
                }
            }
        }

        // Dynamic Genre Tiles
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(title = "Browse All Genres")
            GenreHorizontalGrid(
                genres = exploreViewModel.genres,
                onGenreClick = { /* filter or navigate */ }
            )
        }

        // Trending Tracks Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Top Trending Tracks")
        }

        items(trendingSongs.take(5)) { song ->
            SongListItem(
                song = song,
                isPlaying = playbackState.isPlaying,
                isCurrent = playbackState.currentSong?.id == song.id,
                onClick = {
                    playerViewModel.playSong(song, trendingSongs)
                },
                onFavouriteClick = {
                    playerViewModel.toggleFavourite(song.id)
                }
            )
        }

        // Featured Artists
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(title = "Featured Artists")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(featuredArtists) { artist ->
                    ArtistAvatarItem(
                        artist = artist,
                        onClick = { onArtistClick(artist.id) }
                    )
                }
            }
        }

        // Curated Editorial Playlists
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(title = "Curated Playlists")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(recommendedPlaylists) { playlist ->
                    PlaylistCard(
                        playlist = playlist,
                        onClick = { onPlaylistClick(playlist.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GenreHorizontalGrid(
    genres: List<Genre>,
    onGenreClick: (Genre) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val rows = genres.chunked(2)
        rows.forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                pair.forEach { genre ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(68.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(genre.gradient)
                            .clickable { onGenreClick(genre) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = genre.name,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = genre.iconEmoji,
                                fontSize = 22.sp
                            )
                        }
                    }
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
