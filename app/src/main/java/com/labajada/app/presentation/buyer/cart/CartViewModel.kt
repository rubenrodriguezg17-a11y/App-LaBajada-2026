package com.labajada.app.presentation.buyer.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labajada.app.domain.model.Cart
import com.labajada.app.domain.model.Direccion
import com.labajada.app.domain.model.Order
import com.labajada.app.domain.repository.CartRepository
import com.labajada.app.domain.repository.UserRepository
import com.labajada.app.domain.usecase.cart.AddDishToCartUseCase
import com.labajada.app.domain.usecase.cart.CheckoutCartUseCase
import com.labajada.app.domain.usecase.cart.SetCartDeliveryOptionUseCase
import com.labajada.app.domain.usecase.cart.UpdateCartQuantityUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModel(
    private val cartRepository: CartRepository,
    private val addDishToCartUseCase: AddDishToCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
    private val setCartDeliveryOptionUseCase: SetCartDeliveryOptionUseCase,
    private val checkoutCartUseCase: CheckoutCartUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _buyerId = MutableStateFlow<String?>(null)

    private val _showReviewSheet = MutableStateFlow(false)
    val showReviewSheet: StateFlow<Boolean> = _showReviewSheet.asStateFlow()

    private val _checkoutError = MutableStateFlow<String?>(null)
    val checkoutError: StateFlow<String?> = _checkoutError.asStateFlow()

    private val _isCheckingOut = MutableStateFlow(false)
    val isCheckingOut: StateFlow<Boolean> = _isCheckingOut.asStateFlow()

    val cart: StateFlow<Cart?> = _buyerId
        .flatMapLatest { buyerId ->
            if (buyerId != null) cartRepository.getCart(buyerId)
            else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val itemCount: StateFlow<Int> = cart
        .map { it?.items?.sumOf { item -> item.quantity } ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            val session = userRepository.getActiveSession()
            _buyerId.value = session?.userId
        }
    }

    /**
     * Devuelve el buyerId actual. Si la sesión todavía se está cargando (condición de carrera
     * justo después de crear el ViewModel), espera a que _buyerId reciba su primer valor no nulo
     * en vez de abortar la acción en silencio.
     */
    private suspend fun requireBuyerId(): String? {
        _buyerId.value?.let { return it }
        val session = userRepository.getActiveSession()
        return session?.userId?.also { _buyerId.value = it }
    }

    fun addDish(restaurantId: String, restaurantName: String, dishId: String, dishName: String, unitPrice: Double) {
        viewModelScope.launch {
            val buyerId = requireBuyerId() ?: return@launch
            addDishToCartUseCase(buyerId, restaurantId, restaurantName, dishId, dishName, unitPrice)
        }
    }

    fun updateQuantity(dishId: String, newQuantity: Int) {
        viewModelScope.launch {
            val buyerId = requireBuyerId() ?: return@launch
            updateCartQuantityUseCase(buyerId, dishId, newQuantity)
        }
    }

    fun setDeliverySelected(isDelivery: Boolean) {
        viewModelScope.launch {
            val buyerId = requireBuyerId() ?: return@launch
            setCartDeliveryOptionUseCase(buyerId, isDelivery)
        }
    }

    fun openReviewSheet() = _showReviewSheet.update { true }
    fun closeReviewSheet() = _showReviewSheet.update { false }

    fun confirmarPedido(
        buyerName: String, direccion: Direccion? = null, onSuccess: (Order) -> Unit) {
        viewModelScope.launch {
            val buyerId = requireBuyerId() ?: run {
                _checkoutError.value = "No se pudo verificar tu sesión. Intenta de nuevo."
                return@launch
            }
            _isCheckingOut.value = true
            _checkoutError.value = null
            val result = checkoutCartUseCase(buyerId, buyerName,direccion)
            _isCheckingOut.value = false
            result.onSuccess { order ->
                _showReviewSheet.value = false
                onSuccess(order)
            }.onFailure {
                _checkoutError.value = it.localizedMessage ?: "No se pudo confirmar el pedido."
            }
        }
    }
}