package com.vibe.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibe.music.ui.components.AlbumCard
import com.vibe.music.ui.components.ArtistAvatarItem
import com.vibe.music.ui.components.PlaylistCard
import com.vibe.music.ui.components.SectionHeader
import com.vibe.music.ui.components.SongListItem
import com.vibe.music.ui.theme.VibePrimary
import com.vibe.music.ui.theme.VibeSecondary
import com.vibe.music.ui.theme.VibeSurfaceElevated
import com.vibe.music.ui.theme.VibeTextMuted
import com.vibe.music.ui.theme.VibeTextPrimary
import com.vibe.music.ui.theme.VibeTextSecondary
import com.vibe.music.viewmodel.PlayerViewModel
import com.vibe.music.viewmodel.SearchFilter
import com.vibe.music.viewmodel.SearchViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    playerViewModel: PlayerViewModel,
    onAlbumClick: (String) -> Unit,
    onArtistClick: (String) -> Unit,
    onPlaylistClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val query by searchViewModel.searchQuery.collectAsState()
    val filter by searchViewModel.selectedFilter.collectAsState()
    val history by searchViewModel.searchHistory.collectAsState()
    val songResults by searchViewModel.songResults.collectAsState()
    val albumResults by searchViewModel.albumResults.collectAsState()
    val artistResults by searchViewModel.artistResults.collectAsState()
    val playlistResults by searchViewModel.playlistResults.collectAsState()
    val isLoading by searchViewModel.isLoading.collectAsState()
    val playbackState by playerViewModel.playbackState.collectAsState()

    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
    ) {
        // Search Input Bar
        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { searchViewModel.onQueryChanged(it) },
                placeholder = {
                    Text("Search songs, artists, albums...", color = VibeTextMuted)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = VibeSecondary
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { searchViewModel.onQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear",
                                tint = VibeTextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        searchViewModel.addSearchHistory(query)
                        focusManager.clearFocus()
                    }
                ),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = VibeSurfaceElevated,
                    unfocusedContainerColor = VibeSurfaceElevated,
                    focusedBorderColor = VibeSecondary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = VibeTextPrimary,
                    unfocusedTextColor = VibeTextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SearchFilter.values()) { itemFilter ->
                val isSelected = itemFilter == filter
                AssistChip(
                    onClick = { searchViewModel.selectFilter(itemFilter) },
                    label = {
                        Text(
                            text = itemFilter.label,
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

        Spacer(modifier = Modifier.height(8.dp))

        // Content Area
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = VibeSecondary)
            }
        } else if (query.isBlank()) {
            // Display Search History
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Searches",
                        color = VibeTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (history.isNotEmpty()) {
                        Text(
                            text = "Clear All",
                            color = VibeSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { searchViewModel.clearSearchHistory() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (history.isEmpty()) {
                    Text(
                        text = "No recent searches yet. Search for your favourite Tamil tracks or artists!",
                        color = VibeTextMuted,
                        fontSize = 14.sp
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        history.forEach { item ->
                            AssistChip(
                                onClick = { searchViewModel.onQueryChanged(item) },
                                label = { Text(item, color = VibeTextPrimary) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.History,
                                        contentDescription = null,
                                        tint = VibeTextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = "Remove",
                                        tint = VibeTextMuted,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { searchViewModel.removeSearchHistory(item) }
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = VibeSurfaceElevated
                                ),
                                border = null,
                                shape = CircleShape
                            )
                        }
                    }
                }
            }
        } else {
            // Display Search Results
            val hasResults = songResults.isNotEmpty() || albumResults.isNotEmpty() ||
                    artistResults.isNotEmpty() || playlistResults.isNotEmpty()

            if (!hasResults) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.MusicOff,
                            contentDescription = null,
                            tint = VibeTextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No results found for \"$query\"",
                            color = VibeTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Try searching for a different song, artist or album",
                            color = VibeTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // Song Results
                    if ((filter == SearchFilter.ALL || filter == SearchFilter.SONGS) && songResults.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Songs")
                        }
                        items(songResults) { song ->
                            SongListItem(
                                song = song,
                                isPlaying = playbackState.isPlaying,
                                isCurrent = playbackState.currentSong?.id == song.id,
                                onClick = {
                                    searchViewModel.addSearchHistory(query)
                                    playerViewModel.playSong(song, songResults)
                                },
                                onFavouriteClick = {
                                    playerViewModel.toggleFavourite(song.id)
                                }
                            )
                        }
                    }

                    // Album Results
                    if ((filter == SearchFilter.ALL || filter == SearchFilter.ALBUMS) && albumResults.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            SectionHeader(title = "Albums")
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(albumResults) { album ->
                                    AlbumCard(
                                        album = album,
                                        onClick = {
                                            searchViewModel.addSearchHistory(query)
                                            onAlbumClick(album.id)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Artist Results
                    if ((filter == SearchFilter.ALL || filter == SearchFilter.ARTISTS) && artistResults.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            SectionHeader(title = "Artists")
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(artistResults) { artist ->
                                    ArtistAvatarItem(
                                        artist = artist,
                                        onClick = {
                                            searchViewModel.addSearchHistory(query)
                                            onArtistClick(artist.id)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Playlist Results
                    if ((filter == SearchFilter.ALL || filter == SearchFilter.PLAYLISTS) && playlistResults.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            SectionHeader(title = "Playlists")
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(playlistResults) { pl ->
                                    PlaylistCard(
                                        playlist = pl,
                                        onClick = {
                                            searchViewModel.addSearchHistory(query)
                                            onPlaylistClick(pl.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
