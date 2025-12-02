package com.edu.dam.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

enum class SortBy { DATE, TITLE, FAVORITE }
class UserPrefsRepository(private val context: Context) {
    private object Keys {
        val USER_NAME: Preferences.Key<String> = stringPreferencesKey("user_name")
        val DARK_MODE: Preferences.Key<Boolean> = booleanPreferencesKey("dark_mode")
        val WELCOME_SHOW: Preferences.Key<Boolean> = booleanPreferencesKey("welcome_show")
        val SORT_BY: Preferences.Key<String> = stringPreferencesKey("sort_by")
    }

    val userNameFlow: Flow<String> = context.dataStore.data.map { it[Keys.USER_NAME] ?: "" }
    val darkModeFlow: Flow<Boolean> = context.dataStore.data.map { it[Keys.DARK_MODE] ?: false }
    val welcomeShowFlow: Flow<Boolean> = context.dataStore.data.map { it[Keys.WELCOME_SHOW] ?: false }
    val sortByFlow: Flow<SortBy> = context.dataStore.data.map { prefs ->
        val value = prefs[Keys.SORT_BY]
        runCatching { if (value != null) SortBy.valueOf(value) else null }.getOrNull()
            ?: SortBy.DATE
    }

    suspend fun setUserName (name: String){
        context.dataStore.edit { it[Keys.USER_NAME] = name }
    }

    suspend fun setDarkMode (enable: Boolean){
        context.dataStore.edit { it[Keys.DARK_MODE] = enable }
    }

    suspend fun setWelcomeShow (shown: Boolean){
        context.dataStore.edit { it[Keys.WELCOME_SHOW] = shown }
    }

    suspend fun setSortBy (sortBy: SortBy){
        context.dataStore.edit { it[Keys.SORT_BY] = sortBy.name }
    }
}