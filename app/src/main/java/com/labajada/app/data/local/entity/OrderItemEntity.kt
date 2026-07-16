package com.labajada.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["orderId"])]
)
data class OrderItemEntity (
    @PrimaryKey(autoGenerate = true)
    val itemId: Long = 0,
    val orderId: String,
    val dishId: String,
    val dishName: String,
    val unitPrice: Double,
    val quantity: Int
)
