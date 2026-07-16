package com.labajada.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey
    val buyerId: String,
    val restaurantId: String,
    val restaurantName: String,
    val deliveryCost: Double = 0.0,
    val isDeliverySelected: Boolean = true
)