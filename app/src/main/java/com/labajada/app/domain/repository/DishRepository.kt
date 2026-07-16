package com.labajada.app.domain.repository

import com.labajada.app.domain.model.Dish
import kotlinx.coroutines.flow.Flow

interface DishRepository {
    suspend fun saveDishToMenu(dish: Dish)
    fun getMenuOfTheDay(restaurantId: String): Flow<List<Dish>>
    suspend fun deleteDishFromMenu(dishId: String)
    suspend fun updateDishInMenu(dish: Dish)
    suspend fun getAllMenuDishesOnce(): List<Dish>
}