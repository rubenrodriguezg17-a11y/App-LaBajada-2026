package com.labajada.app.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labajada.app.domain.model.Order
import com.labajada.app.domain.usecase.cart.CheckoutCartUseCase
import kotlinx.coroutines.launch

class OrderViewModel(
    private val checkoutCartUseCase: CheckoutCartUseCase
) : ViewModel() {

    fun confirmarPedido(buyerId: String, buyerName: String, onResult: (Result<Order>) -> Unit) {
        viewModelScope.launch {
            val result = checkoutCartUseCase(buyerId, buyerName)
            onResult(result)
        }
    }
}