package com.labajada.app.domain.usecase.auth

import com.labajada.app.domain.model.User
import com.labajada.app.domain.repository.UserRepository

class LoginUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(email: String, password: String): Result<User> {
        return userRepository.loginWithEmail(email, password)
    }
}