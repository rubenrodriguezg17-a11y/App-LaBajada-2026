package com.labajada.app.domain.model

data class Cart(
    val restaurantId: String,
    val restaurantName: String,
    val deliveryCost: Double,
    val isDeliverySelected: Boolean,
    val items : List<CartItem>
){
    val subtotal: Double get() = items.sumOf { it.subtotal }
    val total: Double get() = subtotal + if (isDeliverySelected) deliveryCost else 0.0
    val isEmpty: Boolean get() = items.isEmpty()
}
