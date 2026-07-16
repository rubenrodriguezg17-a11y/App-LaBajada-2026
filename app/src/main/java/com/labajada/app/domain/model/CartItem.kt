package com.labajada.app.domain.model

data class CartItem(
    val dishId : String,
    val dishName: String,
    val unitPrice: Double,
    val quantity: Int
){
    val subtotal : Double get() = unitPrice * quantity
}
