package com.labajada.app.domain.usecase.search

import com.labajada.app.domain.model.Dish
import com.labajada.app.domain.repository.DishRepository

class GetAllDishesUseCase(
    private val dishRepository: DishRepository
) {
    suspend operator fun invoke(): List<Dish> {
        return dishRepository.getAllMenuDishesOnce()
    }
}