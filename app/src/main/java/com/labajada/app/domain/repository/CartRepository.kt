package com.labajada.app.domain.repository

import com.labajada.app.domain.model.Cart
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCart(buyerId: String): Flow<Cart?>
    suspend fun addDish(
        buyerId: String,
        restaurantId: String,
        restaurantName: String,
        deliveryCost: Double,
        dishId: String,
        dishName: String,
        unitPrice: Double
    )
    suspend fun updateQuantity(buyerId: String, dishId: String, newQuantity: Int)
    suspend fun setDeliverySelected(buyerId: String, isDelivery: Boolean)
    suspend fun clearCart(buyerId: String)
}