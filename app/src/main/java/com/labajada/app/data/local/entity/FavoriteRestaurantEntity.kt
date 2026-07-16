package com.labajada.app.data.local.entity

import androidx.room.Entity

@Entity(tableName = "favorite_restaurants", primaryKeys = ["restaurantId", "buyerId"])
data class FavoriteRestaurantEntity(
    val restaurantId: String,
    val buyerId: String,
    val restaurantName: String,
    val category: String,
    val address: String,
    val timestamp: Long = System.currentTimeMillis()
)