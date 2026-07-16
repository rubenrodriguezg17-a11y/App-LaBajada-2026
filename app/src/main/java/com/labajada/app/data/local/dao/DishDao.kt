package com.labajada.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.labajada.app.data.local.entity.DishEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DishDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuDish(dish: DishEntity)

    @Query("SELECT * FROM restaurant_menu WHERE restaurantId = :restaurantId")
    fun getRestaurantMenu(restaurantId: String): Flow<List<DishEntity>>

    @Query("DELETE FROM restaurant_menu WHERE id = :dishId")
    suspend fun deleteMenuDishById(dishId: String)

    @Query("SELECT * FROM restaurant_menu")
    suspend fun getAllMenuDishesOnce(): List<DishEntity>
}