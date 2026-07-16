package com.labajada.app.domain.usecase.restaurant

import com.labajada.app.domain.model.Restaurant

class GetActiveRestaurantsUseCase {
    operator fun invoke(allRestaurants: List<Restaurant>): List<Restaurant> {
        return allRestaurants.sortedByDescending { it.isOpen }
    }
}