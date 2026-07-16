package com.labajada.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.labajada.app.data.local.entity.FavoriteRestaurantEntity
import com.labajada.app.data.local.entity.RestaurantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurant(restaurant: RestaurantEntity)

    @Query("SELECT * FROM restaurants WHERE isActive = 1")
    fun getAllRestaurants(): Flow<List<RestaurantEntity>>

    @Query("SELECT * FROM restaurants WHERE id = :restaurantId LIMIT 1")
    fun getRestaurantById(restaurantId: String): Flow<RestaurantEntity?>

    @Query("SELECT * FROM restaurants WHERE id = :restaurantId LIMIT 1")
    suspend fun getRestaurantByIdOnce(restaurantId: String): RestaurantEntity?

    // Para encontrar el/los restaurantes de un dueño (una persona podría tener más de uno en el futuro)
    @Query("SELECT * FROM restaurants WHERE ownerId = :ownerId AND isActive = 1")
    fun getRestaurantsByOwner(ownerId: String): Flow<List<RestaurantEntity>>

    @Query("SELECT * FROM restaurants WHERE ownerId = :ownerId AND isActive = 1 LIMIT 1")
    suspend fun getRestaurantByOwnerOnce(ownerId: String): RestaurantEntity?

    @Update
    suspend fun updateRestaurantProfile(restaurant: RestaurantEntity)

    @Query("UPDATE restaurants SET isActive = 0 WHERE id = :restaurantId")
    suspend fun deactivateRestaurant(restaurantId: String)

    // Favoritos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRestaurant(restaurant: FavoriteRestaurantEntity)

    @Query("SELECT * FROM favorite_restaurants WHERE buyerId = :buyerId ORDER BY timestamp DESC")
    fun getAllFavoriteRestaurants(buyerId: String): Flow<List<FavoriteRestaurantEntity>>

    @Query("DELETE FROM favorite_restaurants WHERE restaurantId = :restaurantId AND buyerId = :buyerId")
    suspend fun deleteFavoriteRestaurantById(restaurantId: String, buyerId: String)
}