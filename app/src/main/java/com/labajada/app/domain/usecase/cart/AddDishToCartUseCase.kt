package com.labajada.app.domain.usecase.cart

import com.labajada.app.domain.repository.CartRepository

class AddDishToCartUseCase(private val cartRepository: CartRepository) {

    companion object {
        // TODO: reemplazar por una tarifa configurable por restaurante cuando exista ese campo en Restaurant.
        const val TARIFA_DELIVERY_POR_DEFECTO = 5.0
    }

    suspend operator fun invoke(
        buyerId: String,
        restaurantId: String,
        restaurantName: String,
        dishId: String,
        dishName: String,
        unitPrice: Double
    ) {
        cartRepository.addDish(
            buyerId = buyerId,
            restaurantId = restaurantId,
            restaurantName = restaurantName,
            deliveryCost = TARIFA_DELIVERY_POR_DEFECTO,
            dishId = dishId,
            dishName = dishName,
            unitPrice = unitPrice
        )
    }
}