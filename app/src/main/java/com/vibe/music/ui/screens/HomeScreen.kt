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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vibe.music.data.model.Song
import com.vibe.music.ui.components.AlbumCard
import com.vibe.music.ui.components.ArtistAvatarItem
import com.vibe.music.ui.components.GlassCard
import com.vibe.music.ui.components.PlaylistCard
import com.vibe.music.ui.components.SectionHeader
import com.vibe.music.ui.components.SongListItem
import com.vibe.music.ui.components.VibeGradientButton
import com.vibe.music.ui.theme.PrimaryGradient
import com.vibe.music.ui.theme.VibePrimary
import com.vibe.music.ui.theme.VibeSecondary
import com.vibe.music.ui.theme.VibeSurfaceElevated
import com.vibe.music.ui.theme.VibeTextMuted
import com.vibe.music.ui.theme.VibeTextPrimary
import com.vibe.music.ui.theme.VibeTextSecondary
import com.vibe.music.viewmodel.HomeViewModel
import com.vibe.music.viewmodel.PlayerViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    playerViewModel: PlayerViewModel,
    onAlbumClick: (String) -> Unit,
    onArtistClick: (String) -> Unit,
    onPlaylistClick: (String) -> Unit,
    onNavigateSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val featuredSongs by homeViewModel.featuredSongs.collectAsState()
    val trendingTamil by homeViewModel.trendingTamilSongs.collectAsState()
    val recentlyPlayed by homeViewModel.recentlyPlayed.collectAsState()
    val recommendedAlbums by homeViewModel.recommendedAlbums.collectAsState()
    val popularArtists by homeViewModel.popularArtists.collectAsState()
    val moodPlaylists by homeViewModel.moodPlaylists.collectAsState()
    val playbackState by playerViewModel.playbackState.collectAsState()

    val heroSong = featuredSongs.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // App Header: VIBE Branding & Greeting
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "VIBE",
                            color = VibeSecondary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Filled.ElectricBolt,
                            contentDescription = null,
                            tint = VibePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = homeViewModel.greeting,
                        color = VibeTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = VibeTextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PrimaryGradient)
                            .clickable { onNavigateSettings() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "V",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Hero Featured Card
        if (heroSong != null) {
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp,
                        backgroundColor = Color(0x551E1E2C)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(VibeSurfaceElevated)
                            ) {
                                AsyncImage(
                                    model = heroSong.artworkUrl,
                                    contentDescription = heroSong.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(90.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "FEATURED TRACK",
                                    color = VibeSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = heroSong.title,
                                    color = VibeTextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = heroSong.artist,
                                    color = VibeTextSecondary,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                VibeGradientButton(
                                    text = "Play Now",
                                    icon = Icons.Filled.PlayArrow,
                                    paddingValues = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    onClick = {
                                        playerViewModel.playSong(heroSong, featuredSongs)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Trending Tamil Section
        item {
            SectionHeader(
                title = "Trending Tamil Hits 🔥",
                actionText = "See all",
                onActionClick = { }
            )
        }

        items(trendingTamil.take(4)) { song ->
            SongListItem(
                song = song,
                isPlaying = playbackState.isPlaying,
                isCurrent = playbackState.currentSong?.id == song.id,
                onClick = {
                    playerViewModel.playSong(song, trendingTamil)
                },
                onFavouriteClick = {
                    homeViewModel.toggleFavourite(song.id)
                }
            )
        }

        // Recently Played Carousel
        if (recentlyPlayed.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = "Recently Played 🎧")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(recentlyPlayed) { song ->
                        RecentSongCard(
                            song = song,
                            onClick = {
                                playerViewModel.playSong(song, recentlyPlayed)
                            }
                        )
                    }
                }
            }
        }

        // Recommended Albums Carousel
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(title = "Recommended Albums")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(recommendedAlbums) { album ->
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

        // Popular Artists Carousel
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(title = "Popular Artists")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(popularArtists) { artist ->
                    ArtistAvatarItem(
                        artist = artist,
                        onClick = { onArtistClick(artist.id) }
                    )
                }
            }
        }

        // Mood-Based Playlists
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(title = "Mood & Vibe Playlists")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(moodPlaylists) { playlist ->
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
private fun RecentSongCard(
    song: Song,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(110.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(VibeSurfaceElevated)
        ) {
            AsyncImage(
                model = song.artworkUrl,
                contentDescription = song.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(110.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = song.title,
            color = VibeTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = song.artist,
            color = VibeTextMuted,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
