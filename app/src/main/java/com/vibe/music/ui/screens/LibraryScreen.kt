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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibe.music.ui.components.AlbumCard
import com.vibe.music.ui.components.ArtistAvatarItem
import com.vibe.music.ui.components.PlaylistCard
import com.vibe.music.ui.components.SongListItem
import com.vibe.music.ui.theme.PrimaryGradient
import com.vibe.music.ui.theme.VibePrimary
import com.vibe.music.ui.theme.VibeSecondary
import com.vibe.music.ui.theme.VibeSurface
import com.vibe.music.ui.theme.VibeSurfaceElevated
import com.vibe.music.ui.theme.VibeTextMuted
import com.vibe.music.ui.theme.VibeTextPrimary
import com.vibe.music.ui.theme.VibeTextSecondary
import com.vibe.music.viewmodel.LibraryTab
import com.vibe.music.viewmodel.LibraryViewModel
import com.vibe.music.viewmodel.PlayerViewModel

@Composable
fun LibraryScreen(
    libraryViewModel: LibraryViewModel,
    playerViewModel: PlayerViewModel,
    onAlbumClick: (String) -> Unit,
    onArtistClick: (String) -> Unit,
    onPlaylistClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTab by libraryViewModel.selectedTab.collectAsState()
    val allSongs by libraryViewModel.allSongs.collectAsState()
    val albums by libraryViewModel.albums.collectAsState()
    val artists by libraryViewModel.artists.collectAsState()
    val playlists by libraryViewModel.playlists.collectAsState()
    val favourites by libraryViewModel.favourites.collectAsState()
    val recentlyPlayed by libraryViewModel.recentlyPlayed.collectAsState()
    val playbackState by playerViewModel.playbackState.collectAsState()

    var showCreatePlaylistDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Library",
                    color = VibeTextPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(PrimaryGradient)
                        .clickable { showCreatePlaylistDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "New Playlist",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Tab chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(LibraryTab.values()) { tab ->
                    val isSelected = tab == selectedTab
                    AssistChip(
                        onClick = { libraryViewModel.selectTab(tab) },
                        label = {
                            Text(
                                text = tab.label,
                                color = if (isSelected) Color.White else VibeTextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (isSelected) VibePrimary else VibeSurfaceElevated
                        ),
                        border = null,
                        shape = CircleShape
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Tab Content
            when (selectedTab) {
                LibraryTab.SONGS -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(allSongs) { song ->
                            SongListItem(
                                song = song,
                                isPlaying = playbackState.isPlaying,
                                isCurrent = playbackState.currentSong?.id == song.id,
                                onClick = {
                                    playerViewModel.playSong(song, allSongs)
                                },
                                onFavouriteClick = {
                                    playerViewModel.toggleFavourite(song.id)
                                }
                            )
                        }
                    }
                }

                LibraryTab.ALBUMS -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(albums) { album ->
                            AlbumCard(
                                album = album,
                                onClick = { onAlbumClick(album.id) },
                                cardSize = 160.dp
                            )
                        }
                    }
                }

                LibraryTab.ARTISTS -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(artists) { artist ->
                            ArtistAvatarItem(
                                artist = artist,
                                onClick = { onArtistClick(artist.id) },
                                avatarSize = 80.dp
                            )
                        }
                    }
                }

                LibraryTab.PLAYLISTS -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(playlists) { playlist ->
                            PlaylistCard(
                                playlist = playlist,
                                onClick = { onPlaylistClick(playlist.id) },
                                cardWidth = 160.dp
                            )
                        }
                    }
                }

                LibraryTab.FAVOURITES -> {
                    if (favourites.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No favourite songs yet.\nTap the heart icon on any song to save it here!",
                                color = VibeTextMuted,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(favourites) { song ->
                                SongListItem(
                                    song = song,
                                    isPlaying = playbackState.isPlaying,
                                    isCurrent = playbackState.currentSong?.id == song.id,
                                    onClick = {
                                        playerViewModel.playSong(song, favourites)
                                    },
                                    onFavouriteClick = {
                                        playerViewModel.toggleFavourite(song.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Create Playlist Dialog
        if (showCreatePlaylistDialog) {
            var playlistTitle by remember { mutableStateOf("") }
            var playlistDescription by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showCreatePlaylistDialog = false },
                containerColor = VibeSurface,
                title = {
                    Text(
                        text = "Create Playlist",
                        color = VibeTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = playlistTitle,
                            onValueChange = { playlistTitle = it },
                            placeholder = { Text("Playlist name", color = VibeTextMuted) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = VibeTextPrimary,
                                unfocusedTextColor = VibeTextPrimary,
                                focusedBorderColor = VibeSecondary
                            )
                        )
                        OutlinedTextField(
                            value = playlistDescription,
                            onValueChange = { playlistDescription = it },
                            placeholder = { Text("Description (optional)", color = VibeTextMuted) },
                            maxLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = VibeTextPrimary,
                                unfocusedTextColor = VibeTextPrimary,
                                focusedBorderColor = VibeSecondary
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (playlistTitle.isNotBlank()) {
                                libraryViewModel.createPlaylist(playlistTitle.trim(), playlistDescription.trim())
                                showCreatePlaylistDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VibeSecondary)
                    ) {
                        Text("Create", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreatePlaylistDialog = false }) {
                        Text("Cancel", color = VibeTextSecondary)
                    }
                }
            )
        }
    }
}
