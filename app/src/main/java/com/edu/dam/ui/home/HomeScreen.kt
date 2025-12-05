package com.edu.dam.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.edu.dam.ui.home.components.SwipeableBookCard
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
){
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
    var newNumPage by rememberSaveable { mutableStateOf("") }  // ← String para el TextField

    fun resetForm() {
        newTitle = ""
        newAuthor = ""
        newBody = ""
        newNumPage = ""  // ← Reset a String vacío
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
                FloatingActionButton(onClick = { showSheet = true }) { Text("+") }
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

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (sortedBooks.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(emptyTitle, style = MaterialTheme.typography.titleMedium)
                        Text(emptySubtitle, style = MaterialTheme.typography.bodyMedium)

                        if (!onlyFavorites) {
                            Spacer(Modifier.height(12.dp))
                            OutlinedButton(onClick = { showSheet = true }) {
                                Text("Crear libro")
                            }
                        }
                    }
                }
            } else {
                items(
                    items = sortedBooks,
                    key = { it.id },
                    contentType = { "book" }
                ) { book ->
                    SwipeableBookCard(
                        book = book,
                        onOpen = { nav.navigate(Detail(id = book.id)) },
                        onToggleFavorite = {
                            scope.launch {
                                if (!booksViewModel.toggleFavorite(book.id)) {
                                    snackbarHostState.showSnackbar("No se pudo guardar")
                                }
                            }
                        },
                        onSwipeToDelete = { toDeleteId = book.id },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }

    if (showSheet) {
        AddBookSheet(
            sheetState = sheetState,
            title = newTitle,
            author = newAuthor,
            numPage = newNumPage,  // ← Usar newNumPage (String)
            body = newBody,
            onTitleChange = { newTitle = it },
            onAuthorChange = { newAuthor = it },
            onNumPageChange = { newNumPage = it },  // ← Actualizar newNumPage
            onBodyChange = { newBody = it },
            onCancel = {
                showSheet = false
                resetForm()
            },
            onSave = { isFav ->
                scope.launch {
                    // Convertir String a Int aquí
                    val pages = newNumPage.trim().toIntOrNull() ?: 0  // ← Usar newNumPage

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