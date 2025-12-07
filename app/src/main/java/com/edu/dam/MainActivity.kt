package com.edu.dam

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edu.dam.data.AppState
import com.edu.dam.data.prefs.ThemeMode
import com.edu.dam.data.prefs.UserPrefsRepository
import com.edu.dam.navigation.NavGraph
import com.edu.dam.theme.ComposeNavegationJuanFelipePerezMartinOliverTheme
import com.edu.dam.ui.books.BooksViewModel
import com.edu.dam.ui.books.BooksViewModelFactory

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val nav = rememberNavController()
            val state = remember { AppState() }
            val prefs = remember { UserPrefsRepository(applicationContext) }
            val booksViewModel: BooksViewModel = viewModel(
                factory = BooksViewModelFactory(applicationContext)
            )

            // ═══════════════════════════════════════════════════════════════
            // LÓGICA DE TEMA - Soporta LIGHT, DARK y SYSTEM
            // ═══════════════════════════════════════════════════════════════
            val themeMode by prefs.themeModeFlow.collectAsState(initial = ThemeMode.SYSTEM)
            val isSystemDark = isSystemInDarkTheme()

            val isDarkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemDark
            }

            val savedName by prefs.userNameFlow.collectAsState(initial = "")
            LaunchedEffect(savedName) {
                state.userName.value = savedName
            }

            ComposeNavegationJuanFelipePerezMartinOliverTheme(darkTheme = isDarkTheme) {
                NavGraph(nav, state, prefs, booksViewModel)
            }
        }
    }
}