package com.vibe.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vibe.music.ui.components.SongListItem
import com.vibe.music.ui.components.VibeGradientButton
import com.vibe.music.ui.theme.VibeSecondary
import com.vibe.music.ui.theme.VibeSurfaceElevated
import com.vibe.music.ui.theme.VibeTextMuted
import com.vibe.music.ui.theme.VibeTextPrimary
import com.vibe.music.ui.theme.VibeTextSecondary
import com.vibe.music.viewmodel.PlayerViewModel
import com.vibe.music.viewmodel.PlaylistDetailViewModel

@Composable
fun PlaylistScreen(
    playlistId: String,
    viewModel: PlaylistDetailViewModel,
    playerViewModel: PlayerViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(playlistId) {
        viewModel.loadPlaylist(playlistId)
    }

    val playlist by viewModel.playlist.collectAsState()
    val playbackState by playerViewModel.playbackState.collectAsState()

    if (playlist == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading playlist...", color = VibeTextMuted)
        }
        return
    }

    val curPlaylist = playlist!!

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
    ) {
        // Back Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = VibeTextPrimary
                    )
                }
            }
        }

        // Playlist Artwork Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(VibeSurfaceElevated)
                ) {
                    AsyncImage(
                        model = curPlaylist.artworkUrl,
                        contentDescription = curPlaylist.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = curPlaylist.title,
                    color = VibeTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                if (curPlaylist.description.isNotEmpty()) {
                    Text(
                        text = curPlaylist.description,
                        color = VibeTextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Text(
                    text = "${curPlaylist.songs.size} tracks",
                    color = VibeSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Play / Shuffle Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VibeGradientButton(
                        text = "Play All",
                        icon = Icons.Filled.PlayArrow,
                        onClick = {
                            if (curPlaylist.songs.isNotEmpty()) {
                                playerViewModel.playQueue(curPlaylist.songs, 0)
                            }
                        }
                    )

                    VibeGradientButton(
                        text = "Shuffle",
                        icon = Icons.Filled.Shuffle,
                        gradient = androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(Color(0xFF334155), Color(0xFF1E293B))
                        ),
                        onClick = {
                            if (curPlaylist.songs.isNotEmpty()) {
                                playerViewModel.playQueue(curPlaylist.songs.shuffled(), 0)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Track Listing
        items(curPlaylist.songs) { song ->
            SongListItem(
                song = song,
                isPlaying = playbackState.isPlaying,
                isCurrent = playbackState.currentSong?.id == song.id,
                onClick = {
                    playerViewModel.playSong(song, curPlaylist.songs)
                },
                onFavouriteClick = {
                    playerViewModel.toggleFavourite(song.id)
                }
            )
        }
    }
}
