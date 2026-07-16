package com.labajada.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.labajada.app.data.local.entity.OrderEntity
import com.labajada.app.data.local.entity.OrderItemEntity
import com.labajada.app.data.local.entity.OrderWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Transaction
    suspend fun insertOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>) {
        insertOrder(order)
        insertOrderItems(items)
    }

    @Transaction
    @Query("SELECT * FROM orders_table WHERE restaurantId = :restaurantId AND status != 'ENTREGADO' ORDER BY timestamp ASC")
    fun getActiveOrders(restaurantId: String): Flow<List<OrderWithItems>>

    @Transaction
    @Query("SELECT * FROM orders_table WHERE restaurantId = :restaurantId AND status = 'ENTREGADO'")
    fun getCompletedOrders(restaurantId: String): Flow<List<OrderWithItems>>

    @Transaction
    @Query("SELECT * FROM orders_table WHERE buyerId = :buyerId ORDER BY timestamp DESC")
    fun getOrdersByBuyer(buyerId: String): Flow<List<OrderWithItems>>

    @Query("UPDATE orders_table SET status = :newStatus WHERE id = :orderId")
    suspend fun updateOrderStatusById(orderId: String, newStatus: String)
}