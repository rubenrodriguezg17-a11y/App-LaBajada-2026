package com.labajada.app.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.labajada.app.domain.model.Order
import com.labajada.app.domain.model.OrderStatus

@Entity(tableName = "orders_table")
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val restaurantId: String,
    val buyerId: String,
    val buyerName: String,
    val isDeliverySelected: Boolean,
    val deliveryCost: Double,
    val status: String,
    val timestamp: Long,
    val direccionLat: Double? = null,
    val direccionLng: Double? = null
)
data class OrderWithItems(
    @Embedded val order: OrderEntity,
    @Relation (
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val items: List<OrderItemEntity>
)