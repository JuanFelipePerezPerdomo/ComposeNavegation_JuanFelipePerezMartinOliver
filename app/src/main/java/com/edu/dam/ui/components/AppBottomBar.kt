package com.edu.dam.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.edu.dam.navigation.*

private data class BottomItemTyped(
    val route: RootDestination,
    val label: String,
    val icon: ImageVector
)

@Composable
fun AppBottomBar(
    nav: NavController,
    current: RootDestination
) {
    val items = listOf(
        BottomItemTyped(Home, "Inicio", Icons.Filled.Home),
        BottomItemTyped(Favorites, "Favoritos", Icons.Filled.Favorite),
        BottomItemTyped(Settings, "Ajustes", Icons.Filled.Settings),
    )

    NavigationBar {
        items.forEach { item ->
            val selected = current == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        nav.navigate(item.route) {
                            // evita duplicados si ya estás en ese destino
                            launchSingleTop = true
                            // restaura estado al volver
                            restoreState = true
                            // deja Home como “root” guardando estados de pestañas
                            popUpTo(Home) { saveState = true }
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}