package com.labajada.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurant_menu")
data class DishEntity(
    @PrimaryKey
    val id: String,
    val restaurantId: String,
    val name: String,
    val price: String,
    val imagePath: String
)
