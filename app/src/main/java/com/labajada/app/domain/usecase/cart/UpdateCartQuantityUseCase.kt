package com.labajada.app.domain.usecase.cart

import com.labajada.app.domain.repository.CartRepository

class UpdateCartQuantityUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(buyerId: String, dishId: String, newQuantity: Int) {
        cartRepository.updateQuantity(buyerId, dishId, newQuantity)
    }
}