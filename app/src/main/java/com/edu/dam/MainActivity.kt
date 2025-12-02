package com.edu.dam

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edu.dam.data.AppState
import com.edu.dam.data.prefs.UserPrefsRepository
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
            val booksViewModel: BooksViewModel = viewModel (
                factory = BooksViewModelFactory(applicationContext)
            )

            val darkMode = prefs.darkModeFlow.collectAsState(initial = false).value

            val savedName = prefs.userNameFlow.collectAsState(initial = "").value
            LaunchedEffect(savedName) {
                state.userName.value = savedName
            }
        }
    }
}