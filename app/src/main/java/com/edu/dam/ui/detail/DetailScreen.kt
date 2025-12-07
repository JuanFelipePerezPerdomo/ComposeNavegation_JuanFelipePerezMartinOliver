package com.edu.dam.ui.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.edu.dam.theme.animatedFavoriteColor
import com.edu.dam.ui.books.BooksViewModel
import com.edu.dam.ui.common.formatBookTimestamp
import com.edu.dam.ui.detail.components.EditNoteSheet
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    nav: NavController,
    booksViewModel: BooksViewModel,
    id: String
) {
    val noteState by booksViewModel.observeBook(id).collectAsState(initial = null)
    val currentBook = noteState

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showConfirm by remember { mutableStateOf(false) }

    // --- Edit sheet state ---
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showEdit by remember { mutableStateOf(false) }
    var editTitle by rememberSaveable(currentBook?.title) { mutableStateOf(currentBook?.title.orEmpty()) }
    var editSynopsis by rememberSaveable(currentBook?.synopsis) { mutableStateOf(currentBook?.synopsis.orEmpty()) }
    var editNumPage by rememberSaveable(currentBook?.numPage) { mutableStateOf(currentBook?.numPage ?: 0) }

    // formateo fecha
    val prettyDate = remember(currentBook?.updatedAt) {
        currentBook?.let { formatBookTimestamp(it.updatedAt) } ?: ""
    }

    val starTint = animatedFavoriteColor(currentBook?.isFavorite == true)

    // ID corto para mostrar en el título (primeros 8 caracteres)
    val shortId = remember(id) {
        if (id.length > 8) id.take(8) else id
    }

    LaunchedEffect(currentBook?.id) {
        editTitle = currentBook?.title.orEmpty()
        editSynopsis = currentBook?.synopsis.orEmpty()
        editNumPage = currentBook?.numPage ?: 0
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(currentBook?.title ?: "Detalle #$shortId") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    currentBook?.let { book ->
                        IconButton(onClick = {
                            scope.launch {
                                val success = booksViewModel.toggleFavorite(book.id)
                                if (!success) {
                                    snackbarHostState.showSnackbar("No se pudo guardar")
                                }
                            }
                        }) {
                            Icon(
                                imageVector = if (book.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = if (book.isFavorite) "Quitar de favoritos" else "Marcar como favorito",
                                tint = starTint
                            )
                        }
                        IconButton(onClick = { showEdit = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = { showConfirm = true }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Eliminar libro",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentBook == null) {
                Text("Libro no encontrado", style = MaterialTheme.typography.titleLarge)
                OutlinedButton(onClick = { nav.popBackStack() }) { Text("Volver") }
            } else {
                val book = currentBook

                // ═══════════════════════════════════════════════════════════
                // AUTOR
                // ═══════════════════════════════════════════════════════════
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Autor: ")
                        }
                        append(book.author)
                    },
                    style = MaterialTheme.typography.bodyLarge
                )

                // ═══════════════════════════════════════════════════════════
                // NÚMERO DE PÁGINAS
                // ═══════════════════════════════════════════════════════════
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Número de páginas: ")
                        }
                        append(if (book.numPage > 0) book.numPage.toString() else "No especificado")
                    },
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(Modifier.height(8.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                Spacer(Modifier.height(8.dp))

                // ═══════════════════════════════════════════════════════════
                // SINOPSIS
                // ═══════════════════════════════════════════════════════════
                Text(
                    text = "Sinopsis:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (book.synopsis.isNotBlank()) {
                    Text(
                        text = book.synopsis,
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    Text(
                        text = "Este libro no tiene sinopsis.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ═══════════════════════════════════════════════════════════
                // FECHA (al final, más sutil)
                // ═══════════════════════════════════════════════════════════
                Text(
                    text = "Última actualización: $prettyDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }

    // --- Sheet de edición (state hoisting) ---
    if (showEdit && currentBook != null) {
        val book = currentBook
        EditNoteSheet(
            sheetState = sheetState,
            title = editTitle,
            synopsis = editSynopsis,
            numPage = editNumPage,
            onTitleChange = { editTitle = it },
            onBodyChange = { editSynopsis = it },
            onNumPageChange = { editNumPage = it },
            onCancel = {
                showEdit = false
                editTitle = book.title
                editSynopsis = book.synopsis
                editNumPage = book.numPage
            },
            onSave = {
                scope.launch {
                    val success = booksViewModel.updateBook(
                        id = book.id,
                        title = editTitle.trim(),
                        synopsis = editSynopsis.trim().ifBlank { null },
                        numPage = editNumPage
                    )
                    if (success) {
                        showEdit = false
                        snackbarHostState.showSnackbar("Libro actualizado")
                    } else {
                        snackbarHostState.showSnackbar("No se pudo guardar")
                    }
                }
            },
            onDismissRequest = { showEdit = false }
        )
    }

    if (showConfirm && currentBook != null) {
        val book = currentBook
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Eliminar libro") },
            text = { Text("¿Seguro que quieres eliminarlo? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        val success = booksViewModel.deleteBook(book.id)
                        showConfirm = false
                        if (success) {
                            nav.popBackStack()
                            snackbarHostState.showSnackbar("Libro eliminado")
                        } else {
                            snackbarHostState.showSnackbar("No se pudo eliminar")
                        }
                    }
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Cancelar") }
            }
        )
    }
}