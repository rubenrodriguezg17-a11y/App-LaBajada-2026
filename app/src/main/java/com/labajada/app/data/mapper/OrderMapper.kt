package com.labajada.app.data.mapper

import com.labajada.app.data.local.entity.OrderEntity
import com.labajada.app.data.local.entity.OrderItemEntity
import com.labajada.app.data.local.entity.OrderWithItems
import com.labajada.app.domain.model.Direccion
import com.labajada.app.domain.model.Order
import com.labajada.app.domain.model.OrderItem
import com.labajada.app.domain.model.OrderStatus

fun OrderItem.toEntity(orderId: String) = OrderItemEntity(
    itemId = id,
    orderId = orderId,
    dishId = dishId,
    dishName = dishName,
    unitPrice = unitPrice,
    quantity = quantity
)

fun OrderItemEntity.toDomain() = OrderItem(
    id = itemId,
    dishId = dishId,
    dishName = dishName,
    unitPrice = unitPrice,
    quantity = quantity
)

fun Order.toEntity() = OrderEntity(
    id = id,
    restaurantId = restaurantId,
    buyerId = buyerId,
    buyerName = buyerName,
    isDeliverySelected = isDeliverySelected,
    deliveryCost = deliveryCost,
    status = status.name,
    timestamp = timestamp,
    direccionLat = direccion?.latitud,
    direccionLng = direccion?.longitud
)

fun OrderWithItems.toDomain() = Order(
    id = order.id,
    restaurantId = order.restaurantId,
    buyerId = order.buyerId,
    buyerName = order.buyerName,
    isDeliverySelected = order.isDeliverySelected,
    deliveryCost = order.deliveryCost,
    status = OrderStatus.valueOf(order.status),
    timestamp = order.timestamp,
    items = items.map { it.toDomain() },
    direccion= if (order.direccionLat != null && order.direccionLng != null){
            Direccion(
                latitud = order.direccionLat,
                longitud = order.direccionLng)
    }else null
)