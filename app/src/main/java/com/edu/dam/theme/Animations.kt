package com.edu.dam.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Retorna un color animado para el icono de favorito.
 * - Cuando está marcado como favorito: dorado/amarillo
 * - Cuando no está marcado: gris (onSurfaceVariant)
 */
@Composable
fun animatedFavoriteColor(isFavorite: Boolean): Color {
    val targetColor = if (isFavorite) {
        FavoriteGold
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    }

    val animatedColor = animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 300),
        label = "favoriteColorAnimation"
    )

    return animatedColor.value
}