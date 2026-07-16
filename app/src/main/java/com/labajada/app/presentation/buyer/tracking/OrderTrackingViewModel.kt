package com.labajada.app.presentation.buyer.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labajada.app.domain.model.Order
import com.labajada.app.domain.model.Restaurant
import com.labajada.app.domain.repository.OrderRepository
import com.labajada.app.domain.repository.RestaurantRepository
import com.labajada.app.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class OrderTrackingViewModel(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _buyerId = MutableStateFlow<String?>(null)

    val orders: StateFlow<List<Order>> = _buyerId
        .flatMapLatest { buyerId ->
            if (buyerId != null) orderRepository.getOrdersByBuyer(buyerId)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val restaurantsById: StateFlow<Map<String, Restaurant>> = restaurantRepository.getAllRestaurants()
        .map { list -> list.associateBy { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val activeOrdersCount: StateFlow<Int> = orders
        .map { list -> list.count { it.status != com.labajada.app.domain.model.OrderStatus.ENTREGADO } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            val session = userRepository.getActiveSession()
            _buyerId.value = session?.userId
        }
    }
}