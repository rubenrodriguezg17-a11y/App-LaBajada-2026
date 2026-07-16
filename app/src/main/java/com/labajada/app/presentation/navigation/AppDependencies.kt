package com.labajada.app.presentation.navigation

import android.content.Context
import com.labajada.app.data.local.AppDatabase
import com.labajada.app.data.preferences.UserPreferencesRepositoryImpl
import com.labajada.app.data.repository.*
import com.labajada.app.domain.repository.CartRepository
import com.labajada.app.domain.repository.RestaurantRepository
import com.labajada.app.domain.repository.UserPreferencesRepository
import com.labajada.app.domain.repository.UserRepository
import com.labajada.app.domain.usecase.auth.*
import com.labajada.app.domain.usecase.cart.AddDishToCartUseCase
import com.labajada.app.domain.usecase.cart.CheckoutCartUseCase
import com.labajada.app.domain.usecase.cart.SetCartDeliveryOptionUseCase
import com.labajada.app.domain.usecase.cart.UpdateCartQuantityUseCase
import com.labajada.app.domain.usecase.restaurant.CompleteRestaurantRegistrationUseCase
import com.labajada.app.domain.usecase.restaurant.GetActiveRestaurantsUseCase
import com.labajada.app.domain.usecase.search.*

class AppDependencies(context: Context) {

    private val db = AppDatabase.getDatabase(context)

    // Repositorios
    val userRepository: UserRepository = UserRepositoryImpl(db.userDao())
    val restaurantRepository: RestaurantRepository = RestaurantRepositoryImpl(db.restaurantDao())
    val dishRepository = DishRepositoryImpl(db.dishDao())
    val searchRepository = SearchRepositoryImpl(db.searchDao())
    val orderRepository = OrderRepositoryImpl(db.orderDao())

    // UseCases auth
    val loginUseCase = LoginUseCase(userRepository)
    val loginWithGoogleUseCase = LoginWithGoogleUseCase(userRepository)
    val registerBuyerUseCase = RegisterBuyerUseCase(userRepository)
    val registerRestaurantUseCase = RegisterRestaurantUseCase(userRepository, restaurantRepository)
    val getActiveUserUseCase = GetActiveUserUseCase(userRepository)
    val sendPasswordResetEmailUseCase = SendPasswordResetEmailUseCase(userRepository)
    val userPreferencesRepository: UserPreferencesRepository = UserPreferencesRepositoryImpl(context)
    val logoutUseCase = LogoutUseCase(userRepository, userPreferencesRepository)

    // UseCases search
    val saveSearchQueryUseCase = SaveSearchQueryUseCase(searchRepository)
    val getRecentSearchHistoryUseCase = GetRecentSearchHistoryUseCase(searchRepository)
    val clearSearchHistoryUseCase = ClearSearchHistoryUseCase(searchRepository)
    val manageFavoriteRestaurantUseCase = ManageFavoriteRestaurantUseCase(restaurantRepository)
    val getAllDishesUseCase = GetAllDishesUseCase(dishRepository)
    val getActiveRestaurantsUseCase = GetActiveRestaurantsUseCase()

    val completeRestaurantRegistrationUseCase = CompleteRestaurantRegistrationUseCase(userRepository, restaurantRepository)

    //Carrito de compras
    val cartRepository: CartRepository = CartRepositoryImpl(db.cartDao())

    val addDishToCartUseCase = AddDishToCartUseCase(cartRepository)
    val updateCartQuantityUseCase = UpdateCartQuantityUseCase(cartRepository)
    val setCartDeliveryOptionUseCase = SetCartDeliveryOptionUseCase(cartRepository)
    val checkoutCartUseCase = CheckoutCartUseCase(cartRepository, orderRepository)
}