package com.labajada.app.presentation.restaurant.dashboard

import androidx.lifecycle.viewModelScope
import com.labajada.app.domain.model.Order
import com.labajada.app.domain.model.OrderStatus
import kotlinx.coroutines.launch

/**
 * GESTIÓN DE PEDIDOS (ORDERS)
 */
fun RestaurantDashboardViewModel.cambiarEstadoPedido(order: Order) {
    viewModelScope.launch {
        val siguiente = when (order.status) {
            OrderStatus.ENVIADO -> OrderStatus.PREPARACION
            OrderStatus.PREPARACION -> if (order.isDeliverySelected) OrderStatus.EN_CAMINO else OrderStatus.LISTO_RECOJO
            OrderStatus.EN_CAMINO, OrderStatus.LISTO_RECOJO -> OrderStatus.ENTREGADO
            OrderStatus.ENTREGADO -> return@launch
        }
        orderRepository.updateOrderStatus(order.id, siguiente)
    }
}
