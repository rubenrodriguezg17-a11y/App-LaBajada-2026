package com.labajada.app.domain.repository

interface UserPreferencesRepository {
    suspend fun saveLastSelectedRole(role: String)
    suspend fun getLastSelectedRole(): String?
    suspend fun clearPreferences()
}