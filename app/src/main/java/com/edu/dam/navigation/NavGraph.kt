package com.edu.dam.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.edu.dam.data.AppState
import com.edu.dam.data.prefs.UserPrefsRepository
import com.edu.dam.ui.books.BooksViewModel
import com.edu.dam.ui.login.LoginScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController,
    state: AppState,
    prefs: UserPrefsRepository,
    booksViewModel: BooksViewModel
) {
    NavHost(navController, startDestination = Login){
        composable<Login> { LoginScreen(navController, state, prefs) }

    }
}