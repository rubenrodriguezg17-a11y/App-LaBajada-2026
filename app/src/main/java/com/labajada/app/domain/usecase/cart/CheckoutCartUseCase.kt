package com.labajada.app.domain.usecase.cart

import com.labajada.app.domain.model.Direccion
import com.labajada.app.domain.model.Order
import com.labajada.app.domain.model.OrderItem
import com.labajada.app.domain.repository.CartRepository
import com.labajada.app.domain.repository.OrderRepository
import kotlinx.coroutines.flow.first

class CheckoutCartUseCase(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(buyerId: String, buyerName: String, direccion: Direccion? = null): Result<Order> {
        val cart = cartRepository.getCart(buyerId).first()
            ?: return Result.failure(Exception("El carrito está vacío."))

        if (cart.isEmpty) {
            return Result.failure(Exception("El carrito está vacío."))
        }

        return try {
            val order = Order(
                restaurantId = cart.restaurantId,
                buyerId = buyerId,
                buyerName = buyerName,
                isDeliverySelected = cart.isDeliverySelected,
                deliveryCost = cart.deliveryCost,
                items = cart.items.map {
                    OrderItem(dishId = it.dishId, dishName = it.dishName, unitPrice = it.unitPrice, quantity = it.quantity)
                },
                direccion = direccion
            )
            orderRepository.createOrder(order)
            cartRepository.clearCart(buyerId)
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}