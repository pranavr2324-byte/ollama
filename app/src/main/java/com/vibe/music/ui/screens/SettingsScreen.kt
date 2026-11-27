package com.vibe.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibe.music.ui.components.GlassCard
import com.vibe.music.ui.theme.PrimaryGradient
import com.vibe.music.ui.theme.VibePrimary
import com.vibe.music.ui.theme.VibeSecondary
import com.vibe.music.ui.theme.VibeSurfaceElevated
import com.vibe.music.ui.theme.VibeTextMuted
import com.vibe.music.ui.theme.VibeTextPrimary
import com.vibe.music.ui.theme.VibeTextSecondary
import com.vibe.music.viewmodel.AudioQuality
import com.vibe.music.viewmodel.SettingsViewModel
import com.vibe.music.viewmodel.VibeThemeMode

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val streamingQuality by viewModel.streamingQuality.collectAsState()
    val downloadQuality by viewModel.downloadQuality.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val gapless by viewModel.gaplessPlayback.collectAsState()
    val normalizeVolume by viewModel.normalizeVolume.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
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
            Text(
                text = "Settings",
                color = VibeTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Profile Placeholder Card
        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 16.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(PrimaryGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Music Enthusiast",
                            color = VibeTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "VIBE Premium • Active",
                            color = VibeSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Section: Audio Quality
        SettingsSectionHeader(title = "Audio Quality & Streaming", icon = Icons.Filled.HighQuality)

        AudioQuality.values().forEach { quality ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setStreamingQuality(quality) }
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = streamingQuality == quality,
                    onClick = { viewModel.setStreamingQuality(quality) },
                    colors = RadioButtonDefaults.colors(selectedColor = VibeSecondary)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = quality.title,
                        color = VibeTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = quality.bitrate,
                        color = VibeTextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section: Playback Options
        SettingsSectionHeader(title = "Playback Preferences", icon = Icons.AutoMirrored.Filled.VolumeUp)

        SettingToggleRow(
            title = "Gapless Playback",
            subtitle = "Smooth transitions between sequential tracks",
            checked = gapless,
            onCheckedChange = { viewModel.toggleGapless() }
        )

        SettingToggleRow(
            title = "Normalize Audio Volume",
            subtitle = "Equalize loud and quiet tracks automatically",
            checked = normalizeVolume,
            onCheckedChange = { viewModel.toggleNormalizeVolume() }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Section: Theme & Appearance
        SettingsSectionHeader(title = "Appearance & Theme", icon = Icons.Filled.Palette)

        VibeThemeMode.values().forEach { mode ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setThemeMode(mode) }
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = themeMode == mode,
                    onClick = { viewModel.setThemeMode(mode) },
                    colors = RadioButtonDefaults.colors(selectedColor = VibePrimary)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = mode.title,
                    color = VibeTextPrimary,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section: About
        SettingsSectionHeader(title = "About VIBE", icon = Icons.Filled.Info)

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            Text("VIBE for Android", color = VibeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("Version 1.0.0 • Native Compose Edition", color = VibeTextSecondary, fontSize = 12.sp)
            Text("Powered by AndroidX Media3 ExoPlayer", color = VibeTextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SettingsSectionHeader(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = VibeSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            color = VibeSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = VibeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, color = VibeTextMuted, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VibeSecondary
            )
        )
    }
}
