package com.labajada.app.domain.usecase.auth

import com.labajada.app.domain.repository.UserPreferencesRepository
import com.labajada.app.domain.repository.UserRepository

class LogoutUseCase(
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository

) {
    suspend fun execute() {
        userRepository.logout()
        userPreferencesRepository.clearPreferences()

    }
}