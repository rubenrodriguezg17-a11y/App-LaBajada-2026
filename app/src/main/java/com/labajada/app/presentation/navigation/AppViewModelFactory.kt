package com.labajada.app.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.labajada.app.presentation.buyer.cart.CartViewModel
import com.labajada.app.presentation.buyer.register.BuyerRegisterViewModel
import com.labajada.app.presentation.buyer.search.BuyerSearchViewModel
import com.labajada.app.presentation.buyer.tracking.OrderTrackingViewModel
import com.labajada.app.presentation.login.LoginViewModel
import com.labajada.app.presentation.login.forgot.ForgotPasswordViewModel
import com.labajada.app.presentation.login.verification.EmailVerificationViewModel
import com.labajada.app.presentation.order.OrderViewModel
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel
import com.labajada.app.presentation.restaurant.register.completeRestaurantRegistration.CompleteRestaurantRegistrationViewModel
import com.labajada.app.presentation.restaurant.register.RestaurantRegisterViewModel

class AppViewModelFactory(
    private val deps: AppDependencies
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass.name) {
            LoginViewModel::class.java.name ->
                LoginViewModel(deps.loginUseCase, deps.loginWithGoogleUseCase, deps.userPreferencesRepository, deps.userRepository) as T

            ForgotPasswordViewModel::class.java.name ->
                ForgotPasswordViewModel(deps.sendPasswordResetEmailUseCase) as T

            EmailVerificationViewModel::class.java.name ->
                EmailVerificationViewModel(deps.userRepository) as T

            BuyerRegisterViewModel::class.java.name ->
                BuyerRegisterViewModel(deps.registerBuyerUseCase) as T

            RestaurantRegisterViewModel::class.java.name ->
                RestaurantRegisterViewModel(deps.registerRestaurantUseCase) as T

            CompleteRestaurantRegistrationViewModel::class.java.name ->
                CompleteRestaurantRegistrationViewModel(deps.completeRestaurantRegistrationUseCase) as T

            OrderViewModel::class.java.name ->
                OrderViewModel(checkoutCartUseCase = deps.checkoutCartUseCase) as T

            OrderTrackingViewModel::class.java.name ->
                OrderTrackingViewModel(
                    orderRepository = deps.orderRepository,
                    userRepository = deps.userRepository,
                    restaurantRepository = deps.restaurantRepository
                ) as T

            RestaurantDashboardViewModel::class.java.name ->
                RestaurantDashboardViewModel(
                    restaurantRepository = deps.restaurantRepository,
                    dishRepository = deps.dishRepository,
                    orderRepository = deps.orderRepository,
                    userRepository = deps.userRepository,
                    userPreferencesRepository = deps.userPreferencesRepository,
                    getActiveUserUseCase = deps.getActiveUserUseCase
                ) as T

            BuyerSearchViewModel::class.java.name ->
                BuyerSearchViewModel(
                    saveSearchQueryUseCase = deps.saveSearchQueryUseCase,
                    getRecentSearchHistoryUseCase = deps.getRecentSearchHistoryUseCase,
                    manageFavoriteRestaurantUseCase = deps.manageFavoriteRestaurantUseCase,
                    getActiveUserUseCase = deps.getActiveUserUseCase,
                    clearSearchHistoryUseCase = deps.clearSearchHistoryUseCase,
                    deleteSearchQueryUseCase = deps.deleteSearchQueryUseCase,
                    getAllDishesUseCase = deps.getAllDishesUseCase,
                    dishRepository = deps.dishRepository,
                    restaurantRepository = deps.restaurantRepository,
                    userRepository = deps.userRepository,
                    userPreferencesRepository = deps.userPreferencesRepository,
                    getActiveRestaurantsUseCase = deps.getActiveRestaurantsUseCase
                ) as T

            CartViewModel::class.java.name ->
                CartViewModel(
                    cartRepository = deps.cartRepository,
                    addDishToCartUseCase = deps.addDishToCartUseCase,
                    updateCartQuantityUseCase = deps.updateCartQuantityUseCase,
                    setCartDeliveryOptionUseCase = deps.setCartDeliveryOptionUseCase,
                    checkoutCartUseCase = deps.checkoutCartUseCase,
                    userRepository = deps.userRepository
                ) as T

            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}