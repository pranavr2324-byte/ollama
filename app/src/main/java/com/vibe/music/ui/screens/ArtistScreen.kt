package com.vibe.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vibe.music.ui.components.AlbumCard
import com.vibe.music.ui.components.SectionHeader
import com.vibe.music.ui.components.SongListItem
import com.vibe.music.ui.components.VibeGradientButton
import com.vibe.music.ui.theme.PrimaryGradient
import com.vibe.music.ui.theme.VibeSecondary
import com.vibe.music.ui.theme.VibeSurfaceElevated
import com.vibe.music.ui.theme.VibeTextMuted
import com.vibe.music.ui.theme.VibeTextPrimary
import com.vibe.music.ui.theme.VibeTextSecondary
import com.vibe.music.viewmodel.ArtistDetailViewModel
import com.vibe.music.viewmodel.PlayerViewModel

@Composable
fun ArtistScreen(
    artistId: String,
    viewModel: ArtistDetailViewModel,
    playerViewModel: PlayerViewModel,
    onAlbumClick: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(artistId) {
        viewModel.loadArtist(artistId)
    }

    val artist by viewModel.artist.collectAsState()
    val playbackState by playerViewModel.playbackState.collectAsState()

    if (artist == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading artist...", color = VibeTextMuted)
        }
        return
    }

    val curArtist = artist!!

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

        // Hero Portrait & Details
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .border(3.dp, PrimaryGradient, CircleShape)
                        .background(VibeSurfaceElevated)
                ) {
                    AsyncImage(
                        model = curArtist.imageUrl,
                        contentDescription = curArtist.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = curArtist.name,
                    color = VibeTextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = curArtist.monthlyListeners,
                    color = VibeSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                if (curArtist.bio.isNotEmpty()) {
                    Text(
                        text = curArtist.bio,
                        color = VibeTextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Play / Shuffle
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VibeGradientButton(
                        text = "Play Popular",
                        icon = Icons.Filled.PlayArrow,
                        onClick = {
                            if (curArtist.popularSongs.isNotEmpty()) {
                                playerViewModel.playQueue(curArtist.popularSongs, 0)
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
                            if (curArtist.popularSongs.isNotEmpty()) {
                                playerViewModel.playQueue(curArtist.popularSongs.shuffled(), 0)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Popular Songs Section
        item {
            SectionHeader(title = "Popular Releases")
        }

        items(curArtist.popularSongs) { song ->
            SongListItem(
                song = song,
                isPlaying = playbackState.isPlaying,
                isCurrent = playbackState.currentSong?.id == song.id,
                onClick = {
                    playerViewModel.playSong(song, curArtist.popularSongs)
                },
                onFavouriteClick = {
                    playerViewModel.toggleFavourite(song.id)
                }
            )
        }
    }
}
