package com.labajada.app.domain.usecase.search

import com.labajada.app.domain.model.FavoriteRestaurant
import com.labajada.app.domain.repository.RestaurantRepository
import kotlinx.coroutines.flow.Flow

class ManageFavoriteRestaurantUseCase(private val repository: RestaurantRepository) {

    fun getAll(buyerId: String): Flow<List<FavoriteRestaurant>> {
        return repository.getFavoriteRestaurants(buyerId)
    }

    suspend fun add(id: String, buyerId: String, nombre: String, categoria: String, direccion: String) {
        repository.saveFavoriteRestaurant(
            restaurantId = id,
            buyerId = buyerId,
            restaurantName = nombre,
            category = categoria,
            address = direccion,
            timestamp = System.currentTimeMillis()
        )
    }

    suspend fun remove(restaurantId: String, buyerId: String) {
        repository.removeFavoriteRestaurant(restaurantId, buyerId)
    }
}