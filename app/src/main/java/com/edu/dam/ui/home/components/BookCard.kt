package com.edu.dam.ui.home.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.edu.dam.data.model.Book
import com.edu.dam.theme.animatedFavoriteColor
import com.edu.dam.ui.common.formatBookTimestamp

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookCard(
    book: Book,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prettyDate = remember(book.updatedAt) {
        formatBookTimestamp(book.updatedAt)
    }

    val starColor = animatedFavoriteColor(book.isFavorite)
    val shape = MaterialTheme.shapes.medium

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 140.dp)
            .clip(shape)
            .combinedClickable(
                onClick = onOpen,
                onLongClick = onLongPress
            ),
        shape = shape,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Título
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 28.dp) // Espacio para el icono de favorito
                )

                // Número de páginas (si existe)
                if (book.numPage > 0) {
                    Text(
                        text = "${book.numPage}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Sinopsis (truncada)
                if (book.synopsis.isNotBlank()) {
                    Text(
                        text = book.synopsis,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Espaciador flexible
                Box(modifier = Modifier.weight(1f, fill = false))

                // Autor y fecha
                Text(
                    text = "por ${book.author}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = prettyDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            // Icono de favorito en la esquina superior derecha
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = if (book.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = if (book.isFavorite) "Quitar de favoritos" else "Marcar como favorito",
                    tint = starColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}