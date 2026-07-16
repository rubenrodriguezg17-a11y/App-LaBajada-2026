package com.labajada.app.domain.usecase.restaurant

import com.labajada.app.domain.model.Restaurant
import com.labajada.app.domain.repository.RestaurantRepository
import com.labajada.app.domain.repository.UserRepository

class CompleteRestaurantRegistrationUseCase (
    private val userRepository: UserRepository,
    private val restaurantRepository: RestaurantRepository
){
    suspend fun execute (restaurantData: Restaurant): Result<Unit> {
        val session = userRepository.getActiveSession()
            ?: return Result.failure(Exception("No hay sesion Activa"))

        return try {
            userRepository.activateOwnerRole(session.userId)
            val restaurant = restaurantData.copy(id = session.userId, ownerId =  session.userId)
            restaurantRepository.createRestaurant(restaurant)
            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}