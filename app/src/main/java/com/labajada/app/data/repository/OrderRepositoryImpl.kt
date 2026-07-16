package com.labajada.app.data.repository

import com.labajada.app.data.local.dao.OrderDao
import com.labajada.app.data.mapper.toDomain
import com.labajada.app.data.mapper.toEntity
import com.labajada.app.domain.model.Order
import com.labajada.app.domain.model.OrderStatus
import com.labajada.app.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OrderRepositoryImpl(
    private val orderDao: OrderDao
) : OrderRepository {

    override suspend fun createOrder(order: Order) {
        val orderEntity = order.toEntity()
        val itemEntities = order.items.map { it.toEntity(order.id) }
        orderDao.insertOrderWithItems(orderEntity, itemEntities)
    }

    override fun getActiveOrders(restaurantId: String): Flow<List<Order>> {
        return orderDao.getActiveOrders(restaurantId).map { list -> list.map { it.toDomain() } }
    }

    override fun getCompletedOrders(restaurantId: String): Flow<List<Order>> {
        return orderDao.getCompletedOrders(restaurantId).map { list -> list.map { it.toDomain() } }
    }

    override fun getOrdersByBuyer(buyerId: String): Flow<List<Order>> {
        return orderDao.getOrdersByBuyer(buyerId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        orderDao.updateOrderStatusById(orderId, newStatus.name)
    }
}