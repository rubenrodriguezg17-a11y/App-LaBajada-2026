package com.labajada.app.data.repository

import com.labajada.app.data.local.dao.CartDao
import com.labajada.app.domain.model.Cart
import com.labajada.app.domain.model.CartItem
import com.labajada.app.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class CartRepositoryImpl(
    private val cartDao: CartDao
) : CartRepository {

    override fun getCart(buyerId: String): Flow<Cart?> {
        return cartDao.getCartFlow(buyerId).let { cartFlow ->
            cartFlow.combine(cartDao.getCartItemsFlow(buyerId)) { cartEntity, items ->
                if (cartEntity == null) return@combine null
                Cart(
                    restaurantId = cartEntity.restaurantId,
                    restaurantName = cartEntity.restaurantName,
                    deliveryCost = cartEntity.deliveryCost,
                    isDeliverySelected = cartEntity.isDeliverySelected,
                    items = items.map {
                        CartItem(it.dishId, it.dishName, it.unitPrice, it.quantity)
                    }
                )
            }
        }
    }

    override suspend fun addDish(
        buyerId: String,
        restaurantId: String,
        restaurantName: String,
        deliveryCost: Double,
        dishId: String,
        dishName: String,
        unitPrice: Double
    ) {
        // Delegado a una única transacción atómica de Room (ver CartDao.addOrIncrementDish).
        // Antes, "leer cantidad" y "escribir cantidad" eran dos llamadas suspend separadas
        // desde aquí: si el usuario tocaba "agregar" varias veces rápido, dos corrutinas
        // concurrentes podían leer la misma cantidad y pisarse el incremento una a la otra.
        cartDao.addOrIncrementDish(
            buyerId = buyerId,
            restaurantId = restaurantId,
            restaurantName = restaurantName,
            deliveryCost = deliveryCost,
            dishId = dishId,
            dishName = dishName,
            unitPrice = unitPrice
        )
    }

    override suspend fun updateQuantity(buyerId: String, dishId: String, newQuantity: Int) {
        val item = cartDao.getCartItem(buyerId, dishId) ?: return
        if (newQuantity <= 0) {
            cartDao.deleteItem(item.itemId)
        } else {
            cartDao.updateItemQuantity(item.itemId, newQuantity)
        }
    }

    override suspend fun setDeliverySelected(buyerId: String, isDelivery: Boolean) {
        val cart = cartDao.getCartOnce(buyerId) ?: return
        cartDao.upsertCart(cart.copy(isDeliverySelected = isDelivery))
    }

    override suspend fun clearCart(buyerId: String) {
        cartDao.clearCartCompletely(buyerId)
    }
}