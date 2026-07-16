package com.labajada.app.domain.model

import com.google.firebase.Timestamp


class Order(
    val id: String = java.util.UUID.randomUUID().toString(),
    val restaurantId: String,
    val buyerId: String,
    val buyerName: String,
    val isDeliverySelected: Boolean,
    val deliveryCost: Double = 0.0,
    val status: OrderStatus = OrderStatus.ENVIADO,
    val timestamp: Long = System.currentTimeMillis(),
    val items: List<OrderItem> = emptyList(),
    val direccion: Direccion?= null
) {
    val subtotalItems: Double get() = items.sumOf { it.subtotal }
    val totalPrice: Double get() = subtotalItems + (if (isDeliverySelected) deliveryCost else 0.0)
}