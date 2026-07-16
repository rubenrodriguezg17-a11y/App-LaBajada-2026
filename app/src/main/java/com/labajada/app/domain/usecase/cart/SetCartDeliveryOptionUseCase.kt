package com.labajada.app.domain.usecase.cart

import com.labajada.app.domain.repository.CartRepository

class SetCartDeliveryOptionUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(buyerId: String, isDelivery: Boolean) {
        cartRepository.setDeliverySelected(buyerId, isDelivery)
    }
}