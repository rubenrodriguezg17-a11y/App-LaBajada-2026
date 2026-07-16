package com.labajada.app.domain.repository

import com.labajada.app.domain.model.FavoriteRestaurant
import com.labajada.app.domain.model.Restaurant
import kotlinx.coroutines.flow.Flow

interface RestaurantRepository {
    suspend fun createRestaurant(restaurant: Restaurant)  // ← nuevo, reemplaza el insert que antes vivía en Auth
    suspend fun saveFavoriteRestaurant(
        restaurantId: String,
        buyerId: String,
        restaurantName: String,
        category: String,
        address: String,
        timestamp: Long
    )
    fun getFavoriteRestaurants(buyerId: String): Flow<List<FavoriteRestaurant>>
    suspend fun removeFavoriteRestaurant(restaurantId: String, buyerId: String)
    fun getRestaurantById(restaurantId: String): Flow<Restaurant?>
    fun getRestaurantByOwnerId(ownerId: String): Flow<Restaurant?>  // ← nuevo
    suspend fun updateRestaurantProfile(restaurant: Restaurant)
    fun getAllRestaurants(): Flow<List<Restaurant>>
    suspend fun deactivateRestaurant(restaurantId: String)
}