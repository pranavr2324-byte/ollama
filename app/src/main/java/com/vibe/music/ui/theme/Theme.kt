package com.vibe.music.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val VibeDarkColorScheme = darkColorScheme(
    primary = VibePrimary,
    onPrimary = VibeTextPrimary,
    primaryContainer = VibeSurfaceElevated,
    onPrimaryContainer = VibeTextPrimary,
    secondary = VibeSecondary,
    onSecondary = VibeBackground,
    secondaryContainer = VibeSurfaceGlass,
    onSecondaryContainer = VibeSecondary,
    tertiary = VibeAccentPink,
    background = VibeBackground,
    onBackground = VibeTextPrimary,
    surface = VibeSurface,
    onSurface = VibeTextPrimary,
    surfaceVariant = VibeSurfaceElevated,
    onSurfaceVariant = VibeTextSecondary,
    outline = VibeSurfaceGlassBorder
)

@Composable
fun VibeTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = VibeDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = VibeBackground.toArgb()
                window.navigationBarColor = VibeBackground.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
