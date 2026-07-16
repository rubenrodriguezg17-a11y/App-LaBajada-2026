package com.labajada.app.data.local.dao

import androidx.room.*
import com.labajada.app.data.local.entity.CartEntity
import com.labajada.app.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart WHERE buyerId = :buyerId LIMIT 1")
    fun getCartFlow(buyerId: String): Flow<CartEntity?>

    @Query("SELECT * FROM cart WHERE buyerId = :buyerId LIMIT 1")
    suspend fun getCartOnce(buyerId: String): CartEntity?

    @Insert
    suspend fun insertCart(cart: CartEntity)

    @Query("""
        UPDATE cart SET
            restaurantId = :restaurantId,
            restaurantName = :restaurantName,
            deliveryCost = :deliveryCost,
            isDeliverySelected = :isDeliverySelected
        WHERE buyerId = :buyerId
    """)
    suspend fun updateCart(
        buyerId: String,
        restaurantId: String,
        restaurantName: String,
        deliveryCost: Double,
        isDeliverySelected: Boolean
    )

    @Transaction
    suspend fun upsertCart(cart: CartEntity) {
        val existing = getCartOnce(cart.buyerId)
        if (existing == null) {
            insertCart(cart)
        } else {
            updateCart(
                buyerId = cart.buyerId,
                restaurantId = cart.restaurantId,
                restaurantName = cart.restaurantName,
                deliveryCost = cart.deliveryCost,
                isDeliverySelected = cart.isDeliverySelected
            )
        }
    }

    @Query("DELETE FROM cart WHERE buyerId = :buyerId")
    suspend fun deleteCart(buyerId: String)

    @Query("SELECT * FROM cart_items WHERE cartOwnerId = :buyerId")
    fun getCartItemsFlow(buyerId: String): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE cartOwnerId = :buyerId AND dishId = :dishId LIMIT 1")
    suspend fun getCartItem(buyerId: String, dishId: String): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCartItem(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE itemId = :itemId")
    suspend fun updateItemQuantity(itemId: Long, quantity: Int)

    @Query("DELETE FROM cart_items WHERE itemId = :itemId")
    suspend fun deleteItem(itemId: Long)

    @Query("DELETE FROM cart_items WHERE cartOwnerId = :buyerId")
    suspend fun clearItems(buyerId: String)

    @Transaction
    suspend fun clearCartCompletely(buyerId: String) {
        clearItems(buyerId)
        deleteCart(buyerId)
    }

    /**
     * Agrega (o incrementa) un platillo al carrito como una sola transacción atómica.
     * Evita la condición de carrera de hacer "leer cantidad -> escribir cantidad" desde
     * el repositorio en llamadas separadas: si el usuario toca "agregar" varias veces
     * rápido, cada llamada a este método se serializa dentro de la transacción de Room
     * y no se pierden incrementos.
     *
     * Devuelve true si se tuvo que reemplazar el carrito de otro restaurante (para que el
     * caller pueda avisar al usuario si lo desea).
     */
    @Transaction
    suspend fun addOrIncrementDish(
        buyerId: String,
        restaurantId: String,
        restaurantName: String,
        deliveryCost: Double,
        dishId: String,
        dishName: String,
        unitPrice: Double
    ): Boolean {
        val existingCart = getCartOnce(buyerId)
        val cartReemplazado = existingCart != null && existingCart.restaurantId != restaurantId

        if (cartReemplazado) {
            clearItems(buyerId)
            deleteCart(buyerId)
        }

        val esMismoCarrito = existingCart != null && existingCart.restaurantId == restaurantId
        upsertCart(
            CartEntity(
                buyerId = buyerId,
                restaurantId = restaurantId,
                restaurantName = restaurantName,
                deliveryCost = if (esMismoCarrito) existingCart!!.deliveryCost else deliveryCost,
                isDeliverySelected = existingCart?.isDeliverySelected?.takeIf { esMismoCarrito } ?: true
            )
        )

        val existingItem = getCartItem(buyerId, dishId)
        if (existingItem != null) {
            updateItemQuantity(existingItem.itemId, existingItem.quantity + 1)
        } else {
            upsertCartItem(
                CartItemEntity(
                    cartOwnerId = buyerId,
                    dishId = dishId,
                    dishName = dishName,
                    unitPrice = unitPrice,
                    quantity = 1
                )
            )
        }

        return cartReemplazado
    }
}