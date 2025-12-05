package com.edu.dam.ui.home.components
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.edu.dam.data.model.Book

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SwipeableBookCard(
    book: Book,
    onOpen:()-> Unit,
    onToggleFavorite: ()->Unit,
    onSwipeToDelete:() ->Unit,
    modifier: Modifier = Modifier
){
    val dismissState= rememberSwipeToDismissBoxState(
        confirmValueChange={dismissValue ->
            if(dismissValue == SwipeToDismissBoxValue.EndToStart){
                onSwipeToDelete()
                false
            } else{
                false
            }
        },
        positionalThreshold = {distance -> distance * 0.33f}
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            SwipeDeleteBackground(progress = dismissState.progress)
        },
        content = {
            BookCard(
                book = book,
                onOpen = onOpen,
                onToggleFavorite = onToggleFavorite
            )
        }
    )
}

@Composable
private fun SwipeDeleteBackground(progress: Float) {
    val normalizedProgress = progress.coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 2.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Red.copy(alpha = 0.6f * normalizedProgress)
            )
        ) { /* Card vacía solo para el fondo */ }

        // Icono de papelera que se desvanece con el progreso
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = Color.White.copy(alpha = normalizedProgress),
            modifier = Modifier
                .padding(end = 20.dp)
                .size(28.dp)
        )
    }
}