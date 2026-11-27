package com.vibe.music.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AudioQuality(val title: String, val bitrate: String) {
    AUTO("Automatic", "Dynamically adjusts"),
    NORMAL("Normal", "128 kbps (Data Saver)"),
    HIGH("High", "256 kbps (AAC)"),
    LOSSLESS("Lossless", "320 kbps / FLAC")
}

enum class VibeThemeMode(val title: String) {
    CHARCOAL_BLACK("Nocturnal Charcoal (Default)"),
    OBSIDIAN_GLOW("Deep Obsidian Glow"),
    CYBER_NEON("Cyberpunk Neon")
}

class SettingsViewModel : ViewModel() {

    private val _streamingQuality = MutableStateFlow(AudioQuality.HIGH)
    val streamingQuality: StateFlow<AudioQuality> = _streamingQuality.asStateFlow()

    private val _downloadQuality = MutableStateFlow(AudioQuality.LOSSLESS)
    val downloadQuality: StateFlow<AudioQuality> = _downloadQuality.asStateFlow()

    private val _themeMode = MutableStateFlow(VibeThemeMode.CHARCOAL_BLACK)
    val themeMode: StateFlow<VibeThemeMode> = _themeMode.asStateFlow()

    private val _gaplessPlayback = MutableStateFlow(true)
    val gaplessPlayback: StateFlow<Boolean> = _gaplessPlayback.asStateFlow()

    private val _normalizeVolume = MutableStateFlow(true)
    val normalizeVolume: StateFlow<Boolean> = _normalizeVolume.asStateFlow()

    fun setStreamingQuality(quality: AudioQuality) {
        _streamingQuality.value = quality
    }

    fun setDownloadQuality(quality: AudioQuality) {
        _downloadQuality.value = quality
    }

    fun setThemeMode(theme: VibeThemeMode) {
        _themeMode.value = theme
    }

    fun toggleGapless() {
        _gaplessPlayback.value = !_gaplessPlayback.value
    }

    fun toggleNormalizeVolume() {
        _normalizeVolume.value = !_normalizeVolume.value
    }
}
