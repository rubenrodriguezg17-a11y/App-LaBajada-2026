package com.labajada.app.domain.usecase.auth

import com.labajada.app.domain.model.GoogleLoginResult
import com.labajada.app.domain.repository.UserRepository

class LoginWithGoogleUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(idToken: String): Result<GoogleLoginResult> {
        return userRepository.loginWithGoogle(idToken)
    }
}