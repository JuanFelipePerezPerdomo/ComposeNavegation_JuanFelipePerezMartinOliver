package com.edu.dam.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.edu.dam.data.AppState
import com.edu.dam.data.prefs.UserPrefsRepository
import com.edu.dam.ui.books.BooksViewModel
import com.edu.dam.ui.home.HomeScreen
import com.edu.dam.ui.login.LoginScreen
import com.edu.dam.ui.detail.DetailScreen
import com.edu.dam.ui.settings.SettingsScreen

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
        composable<Home> { HomeScreen(navController, state, prefs, booksViewModel) }
        composable<Favorites> {
            HomeScreen(
                nav = navController,
                state = state,
                prefs = prefs,
                booksViewModel = booksViewModel,
                onlyFavorites = true
            )
        }
        composable<Settings> { SettingsScreen(navController, state, prefs) }
        composable<Detail> { backStack ->
            val args = backStack.toRoute<Detail>()  // args.id
            DetailScreen(navController, booksViewModel, id = args.id)
        }
    }
}