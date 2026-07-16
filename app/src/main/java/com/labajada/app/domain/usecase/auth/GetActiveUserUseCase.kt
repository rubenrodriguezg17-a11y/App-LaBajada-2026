package com.labajada.app.domain.usecase.auth

import com.labajada.app.domain.model.User
import com.labajada.app.domain.repository.UserRepository

class GetActiveUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): User? {
        val session = userRepository.getActiveSession() ?: return null
        return userRepository.getUserById(session.userId)
    }
}