package com.edu.dam.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute

sealed interface RootDestination: AppRoute

@Serializable
data object Login: AppRoute

@Serializable
data object Home: RootDestination

@Serializable
data object Favorites: RootDestination

@Serializable
data object Settings: RootDestination

@Serializable
data class Detail(val id: String) : AppRoute