package com.edu.dam.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

@Composable
fun animatedFavoriteColor(isFavorite: Boolean): Color {
    val starColor by animateColorAsState(
        targetValue = if (isFavorite)
            Color(0xFFFFB300) // ámbar vivo (más visible)
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        label = "favoriteStarColor"
    )
    return starColor
}