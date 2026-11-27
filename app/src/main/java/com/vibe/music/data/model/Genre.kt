package com.vibe.music.data.model

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class Genre(
    val id: String,
    val name: String,
    val gradient: Brush,
    val iconEmoji: String = "🎵"
) {
    companion object {
        fun defaultGenres(): List<Genre> = listOf(
            Genre("tamil_hits", "Tamil Hits", Brush.linearGradient(listOf(Color(0xFFFF5722), Color(0xFFFF9800))), "🔥"),
            Genre("indie", "Tamil Indie", Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))), "🎸"),
            Genre("synthwave", "Synthwave", Brush.linearGradient(listOf(Color(0xFF06B6D4), Color(0xFF3B82F6))), "🌌"),
            Genre("lofi", "Lo-Fi Beats", Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF059669))), "☕"),
            Genre("workout", "EDM & Workout", Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFFB91C1C))), "⚡"),
            Genre("romantic", "Melody & Romance", Brush.linearGradient(listOf(Color(0xFFF43F5E), Color(0xFFBE185D))), "💖"),
            Genre("acoustic", "Acoustic Sunset", Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))), "🌅"),
            Genre("classical", "Carnatic Fusion", Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF4338CA))), "🪕")
        )
    }
}
