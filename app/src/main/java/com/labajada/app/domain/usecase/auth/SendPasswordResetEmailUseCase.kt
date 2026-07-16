package com.labajada.app.domain.usecase.auth

import com.labajada.app.core.validation.PasswordValidator
import com.labajada.app.domain.repository.UserRepository

class SendPasswordResetEmailUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank()) {
            return Result.failure(Exception("Ingresa tu correo electrónico"))
        }
        if (!PasswordValidator.isValidEmail(email)) {
            return Result.failure(Exception("Ingresa un correo electrónico válido"))
        }
        return userRepository.sendPasswordResetEmail(email.trim())
    }
}