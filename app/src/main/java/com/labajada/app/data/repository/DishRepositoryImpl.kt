package com.labajada.app.data.repository

import com.labajada.app.data.local.dao.DishDao
import com.labajada.app.data.mapper.toDomain
import com.labajada.app.data.mapper.toEntity
import com.labajada.app.domain.model.Dish
import com.labajada.app.domain.repository.DishRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DishRepositoryImpl(
    private val dishDao: DishDao
) : DishRepository {

    override suspend fun saveDishToMenu(dish: Dish) {
        dishDao.insertMenuDish(dish.toEntity())
    }

    override fun getMenuOfTheDay(restaurantId: String): Flow<List<Dish>> {
        return dishDao.getRestaurantMenu(restaurantId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteDishFromMenu(dishId: String) {
        dishDao.deleteMenuDishById(dishId)
    }

    override suspend fun updateDishInMenu(dish: Dish) {
        dishDao.insertMenuDish(dish.toEntity())
    }

    override suspend fun getAllMenuDishesOnce(): List<Dish> {
        return dishDao.getAllMenuDishesOnce().map { it.toDomain() }
    }
}