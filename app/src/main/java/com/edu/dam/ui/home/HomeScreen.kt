package com.edu.dam.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.edu.dam.data.AppState
import com.edu.dam.data.prefs.SortBy
import com.edu.dam.data.prefs.UserPrefsRepository
import com.edu.dam.navigation.Detail
import com.edu.dam.navigation.Favorites
import com.edu.dam.navigation.Home
import com.edu.dam.navigation.Login
import com.edu.dam.ui.books.BooksViewModel
import com.edu.dam.ui.components.AppBottomBar
import com.edu.dam.ui.home.components.AddBookSheet
import com.edu.dam.ui.home.components.BookCard
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    nav: NavController,
    state: AppState,
    prefs: UserPrefsRepository,
    booksViewModel: BooksViewModel,
    onlyFavorites: Boolean = false
) {
    val name by state.userName.collectAsState()
    val books by booksViewModel.books.collectAsState()
    val sortBy by prefs.sortByFlow.collectAsState(initial = SortBy.DATE)
    val welcomeShown by prefs.welcomeShowFlow.collectAsState(initial = false)

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var toDeleteId by remember { mutableStateOf<String?>(null) }
    var loggingOut by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    var newTitle by rememberSaveable { mutableStateOf("") }
    var newAuthor by rememberSaveable { mutableStateOf("") }
    var newBody by rememberSaveable { mutableStateOf("") }
    var newNumPage by rememberSaveable { mutableStateOf("") }

    fun resetForm() {
        newTitle = ""
        newAuthor = ""
        newBody = ""
        newNumPage = ""
    }

    // Diálogo de bienvenida
    if (!welcomeShown && !onlyFavorites) {
        AlertDialog(
            onDismissRequest = {
                scope.launch { prefs.setWelcomeShow(true) }
            },
            title = { Text("¡Bienvenido!") },
            text = {
                Text("Esta app demuestra navegación tipada, estado con Flow y persistencia.")
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { prefs.setWelcomeShow(true) }
                }) {
                    Text("Entendido")
                }
            }
        )
    }

    val topTitle by remember(onlyFavorites, name) {
        mutableStateOf(
            (if (onlyFavorites) "Favoritos" else "Libros") +
                    " — " + name.ifBlank { "invitado" }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(topTitle) },
                actions = {
                    IconButton(
                        onClick = {
                            nav.navigate(Login) {
                                popUpTo(Home) {
                                    inclusive = true
                                }
                            }
                            loggingOut = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (!onlyFavorites) {
                FloatingActionButton(
                    onClick = { showSheet = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Text("+", style = MaterialTheme.typography.headlineSmall)
                }
            }
        },
        bottomBar = { AppBottomBar(nav = nav, current = if (onlyFavorites) Favorites else Home) }
    ) { innerPadding ->
        val sortedBooks by remember(books, onlyFavorites, sortBy) {
            derivedStateOf {
                val base = if (onlyFavorites) books.filter { it.isFavorite } else books
                when (sortBy) {
                    SortBy.DATE -> base.sortedByDescending { it.updatedAt }
                    SortBy.TITLE -> base.sortedBy { it.title.lowercase() }
                    SortBy.FAVORITE -> base.sortedWith(
                        compareByDescending<com.edu.dam.data.model.Book> { it.isFavorite }
                            .thenByDescending { it.updatedAt }
                    )
                }
            }
        }

        val (emptyTitle, emptySubtitle) = remember(onlyFavorites) {
            if (onlyFavorites)
                "No hay favoritos aún" to "Marca libros con ★ para verlos aquí"
            else
                "Aún no hay libros" to "Pulsa + para crear el primero"
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // ═══════════════════════════════════════════════════════════════
            // FILTROS - Solo Fecha y Título (sin Favoritos)
            // ═══════════════════════════════════════════════════════════════
            FilterChipsRow(
                currentSortBy = sortBy,
                onSortByChange = { newSort ->
                    scope.launch { prefs.setSortBy(newSort) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ═══════════════════════════════════════════════════════════════
            // GRID DE LIBROS - 3 columnas
            // ═══════════════════════════════════════════════════════════════
            if (sortedBooks.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        emptyTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        emptySubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )

                    if (!onlyFavorites) {
                        Spacer(Modifier.height(16.dp))
                        FilledTonalButton(onClick = { showSheet = true }) {
                            Text("Crear libro")
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(
                        items = sortedBooks,
                        key = { it.id }
                    ) { book ->
                        BookCard(
                            book = book,
                            onOpen = { nav.navigate(Detail(id = book.id)) },
                            onToggleFavorite = {
                                scope.launch {
                                    if (!booksViewModel.toggleFavorite(book.id)) {
                                        snackbarHostState.showSnackbar("No se pudo guardar")
                                    }
                                }
                            },
                            onLongPress = { toDeleteId = book.id }
                        )
                    }
                }
            }
        }
    }

    if (showSheet) {
        AddBookSheet(
            sheetState = sheetState,
            title = newTitle,
            author = newAuthor,
            numPage = newNumPage,
            body = newBody,
            onTitleChange = { newTitle = it },
            onAuthorChange = { newAuthor = it },
            onNumPageChange = { newNumPage = it },
            onBodyChange = { newBody = it },
            onCancel = {
                showSheet = false
                resetForm()
            },
            onSave = { isFav ->
                scope.launch {
                    val pages = newNumPage.trim().toIntOrNull() ?: 0

                    val success = booksViewModel.addBook(
                        title = newTitle.trim(),
                        author = newAuthor.trim(),
                        numPage = pages,
                        synopsis = newBody.trim().ifBlank { null },
                        isFavorite = isFav
                    )
                    if (success) {
                        showSheet = false
                        resetForm()
                        snackbarHostState.showSnackbar("Libro creado")
                    } else {
                        snackbarHostState.showSnackbar("No se pudo guardar")
                    }
                }
            },
            onDismissRequest = {
                showSheet = false
                resetForm()
            }
        )
    }

    if (toDeleteId != null) {
        AlertDialog(
            onDismissRequest = { toDeleteId = null },
            title = { Text("Eliminar libro") },
            text = {
                Text("¿Seguro que quieres eliminar este libro? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(onClick = {
                    val id = toDeleteId!!
                    scope.launch {
                        val success = booksViewModel.deleteBook(id)
                        toDeleteId = null
                        snackbarHostState.showSnackbar(
                            if (success) "Libro eliminado" else "No se pudo eliminar"
                        )
                    }
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { toDeleteId = null }) { Text("Cancelar") }
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            if (loggingOut)
                state.resetForLogout()
        }
    }
}
private data class FilterChipData(
    val sortBy: SortBy,
    val label: String,
    val icon: ImageVector
)

@Composable
private fun FilterChipsRow(
    currentSortBy: SortBy,
    onSortByChange: (SortBy) -> Unit,
    modifier: Modifier = Modifier
) {
    // Solo Fecha y Título (sin Favoritos)
    val chips = listOf(
        FilterChipData(SortBy.DATE, "Fecha", Icons.Filled.CalendarMonth),
        FilterChipData(SortBy.TITLE, "Título", Icons.Filled.SortByAlpha)
    )

    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Sort,
            contentDescription = "Ordenar por",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = "Filtros:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(4.dp))

        chips.forEach { chip ->
            val isSelected = currentSortBy == chip.sortBy

            val containerColor by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                },
                animationSpec = tween(durationMillis = 200),
                label = "chipContainerColor"
            )

            val contentColor by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                animationSpec = tween(durationMillis = 200),
                label = "chipContentColor"
            )

            FilterChip(
                selected = isSelected,
                onClick = { onSortByChange(chip.sortBy) },
                label = {
                    Text(
                        text = chip.label,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = chip.icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = containerColor,
                    labelColor = contentColor,
                    iconColor = contentColor,
                    selectedContainerColor = containerColor,
                    selectedLabelColor = contentColor,
                    selectedLeadingIconColor = contentColor
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (isSelected) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    },
                    selectedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    borderWidth = 1.dp,
                    selectedBorderWidth = 1.dp,
                    enabled = true,
                    selected = isSelected
                )
            )
        }
    }
}