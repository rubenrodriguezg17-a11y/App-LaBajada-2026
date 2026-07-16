package com.labajada.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.labajada.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "la_bajada_user_prefs")

class UserPreferencesRepositoryImpl(
    private val context: Context
) : UserPreferencesRepository {

    companion object {
        private val LAST_ROLE_KEY = stringPreferencesKey("last_selected_role")
    }

    override suspend fun saveLastSelectedRole(role: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_ROLE_KEY] = role
        }
    }

    override suspend fun getLastSelectedRole(): String? {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }
            .map { preferences -> preferences[LAST_ROLE_KEY] }
            .first()
    }

    override suspend fun clearPreferences() {
        context.dataStore.edit { it.clear() }
    }
}