package com.labajada.app.domain.usecase.auth

import com.labajada.app.domain.model.Restaurant
import com.labajada.app.domain.model.User
import com.labajada.app.domain.repository.RestaurantRepository
import com.labajada.app.domain.repository.UserRepository

class RegisterRestaurantUseCase(
    private val userRepository: UserRepository,
    private val restaurantRepository: RestaurantRepository
) {
    suspend fun execute(
        email: String,
        password: String,
        ownerFullName: String,
        ownerPhone: String,
        restaurantData: Restaurant
    ): Result<User> {
        // 1. Crear la persona (User) con isOwner = true
        val userResult = userRepository.registerWithEmail(
            email = email,
            password = password,
            fullName = ownerFullName,
            phoneNumber = ownerPhone,
            isBuyer = false,
            isOwner = true
        )

        val user = userResult.getOrElse { return Result.failure(it) }

        // 2. Crear el negocio (Restaurant) amarrado a ese User vía ownerId
        return try {
            val restaurantConDueño = restaurantData.copy(
                id = user.uid,        // el restaurante usa el mismo UID como ID por simplicidad (1 dueño = 1 local por ahora)
                ownerId = user.uid
            )
            restaurantRepository.createRestaurant(restaurantConDueño)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}