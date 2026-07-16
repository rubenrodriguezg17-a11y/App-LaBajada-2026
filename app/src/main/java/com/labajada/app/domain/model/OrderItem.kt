package com.labajada.app.domain.model

data class OrderItem (
    val id: Long = 0,
    val dishId: String,
    val dishName: String,
    val unitPrice: Double,
    val quantity: Int
){
    val subtotal: Double get() = unitPrice * quantity
}