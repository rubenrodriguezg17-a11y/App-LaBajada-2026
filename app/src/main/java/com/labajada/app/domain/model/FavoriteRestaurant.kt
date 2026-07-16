package com.labajada.app.domain.model

data class FavoriteRestaurant(
    val restaurantId: String,
    val buyerId: String,
    val restaurantName: String,
    val category: String,
    val address: String,
    val timestamp: Long
)