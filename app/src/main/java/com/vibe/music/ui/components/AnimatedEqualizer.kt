package com.vibe.music.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vibe.music.ui.theme.VibeSecondary

@Composable
fun AnimatedEqualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barColor: Color = VibeSecondary,
    barWidth: Dp = 3.dp,
    maxHeight: Dp = 16.dp
) {
    val transition = rememberInfiniteTransition(label = "equalizer")

    val h1 by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = if (isPlaying) 1f else 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "h1"
    )
    val h2 by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = if (isPlaying) 0.3f else 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, delayMillis = 100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "h2"
    )
    val h3 by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = if (isPlaying) 0.95f else 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 50, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "h3"
    )

    Row(
        modifier = modifier.height(maxHeight),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .width(barWidth)
                .height(maxHeight * (if (isPlaying) h1 else 0.25f))
                .clip(RoundedCornerShape(1.dp))
                .background(barColor)
        )
        Box(
            modifier = Modifier
                .width(barWidth)
                .height(maxHeight * (if (isPlaying) h2 else 0.5f))
                .clip(RoundedCornerShape(1.dp))
                .background(barColor)
        )
        Box(
            modifier = Modifier
                .width(barWidth)
                .height(maxHeight * (if (isPlaying) h3 else 0.35f))
                .clip(RoundedCornerShape(1.dp))
                .background(barColor)
        )
    }
}
