package com.labajada.app.presentation.restaurant.dashboard

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * ROLES DE USUARIO Y CUENTA (dual rol comprador/vendedor, desactivación de cuenta)
 */
fun RestaurantDashboardViewModel.switchToBuyerMode(onSwitched: () -> Unit) {
    viewModelScope.launch {
        userPreferencesRepository.saveLastSelectedRole("BUYER")
        onSwitched()
    }
}

suspend fun RestaurantDashboardViewModel.deactivateAccountAndRestaurant(password: String): Result<Unit> {
    val result = userRepository.deactivateAccount(password)
    return result.onSuccess {
        restaurantRepository.deactivateRestaurant(currentRestaurantId)
    }
}
