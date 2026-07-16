package com.labajada.app.domain.usecase.auth

import com.labajada.app.domain.model.User
import com.labajada.app.domain.repository.UserRepository

class RegisterBuyerUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(email: String, password: String, fullName: String, phoneNumber: String): Result<User> {
        return userRepository.registerWithEmail(
            email = email,
            password = password,
            fullName = fullName,
            phoneNumber = phoneNumber,
            isBuyer = true,
            isOwner = false
        )
    }
}