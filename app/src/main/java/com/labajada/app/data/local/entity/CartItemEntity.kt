package com.labajada.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = CartEntity::class,
            parentColumns = ["buyerId"],
            childColumns = ["cartOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val itemId: Long = 0,
    val cartOwnerId: String,
    val dishId: String,
    val dishName: String,
    val unitPrice: Double,
    val quantity: Int
)