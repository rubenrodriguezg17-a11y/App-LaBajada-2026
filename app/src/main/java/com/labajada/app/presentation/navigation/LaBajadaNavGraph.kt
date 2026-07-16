package com.labajada.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.labajada.app.presentation.buyer.cart.CartViewModel
import com.labajada.app.presentation.buyer.dashboard.BuyerDashboardScreen
import com.labajada.app.presentation.buyer.map.BuyerMapScreen
import com.labajada.app.presentation.buyer.register.BuyerRegisterScreen
import com.labajada.app.presentation.buyer.register.BuyerRegisterViewModel
import com.labajada.app.presentation.buyer.search.BuyerSearchViewModel
import com.labajada.app.presentation.buyer.tracking.OrderTrackingViewModel
import com.labajada.app.presentation.login.LoginScreen
import com.labajada.app.presentation.login.LoginViewModel
import com.labajada.app.presentation.login.forgot.ForgotPasswordScreen
import com.labajada.app.presentation.login.forgot.ForgotPasswordViewModel
import com.labajada.app.presentation.login.verification.EmailVerificationScreen
import com.labajada.app.presentation.login.verification.EmailVerificationViewModel
import com.labajada.app.presentation.onboarding.OnboardingScreen
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardScreen
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel
import com.labajada.app.presentation.restaurant.register.completeRestaurantRegistration.CompleteRestaurantRegistrationScreen
import com.labajada.app.presentation.restaurant.register.completeRestaurantRegistration.CompleteRestaurantRegistrationViewModel
import com.labajada.app.presentation.restaurant.register.RestaurantRegisterScreen
import com.labajada.app.presentation.restaurant.register.RestaurantRegisterViewModel
import kotlinx.coroutines.launch

sealed interface NavUiState {
    object Loading : NavUiState
    data class Success(val startDestination: String) : NavUiState
}

@Composable
fun LaBajadaNavGraph() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val deps = remember { AppDependencies(context) }
    val factory = remember { AppViewModelFactory(deps) }

    var checkSessionTrigger by remember { mutableStateOf(0) }
    var uiState by remember { mutableStateOf<NavUiState>(NavUiState.Loading) }

    LaunchedEffect(checkSessionTrigger) {
        val session = deps.userRepository.getActiveSession()
        val destino = if (session != null) {
            val user = deps.userRepository.getUserById(session.userId)
            when {
                user == null -> Screen.Login.route

                !deps.userRepository.isCurrentUserEmailVerified() -> {
                    // Cuenta con sesión guardada pero correo aún sin confirmar:
                    // no la dejamos pasar a Home, la mandamos a verificar.
                    val rolDestino = when {
                        user.isOwner && !user.isBuyer -> Screen.RestaurantHome.route
                        user.isBuyer && !user.isOwner -> Screen.BuyerHome.route
                        else -> {
                            val lastRole = deps.userPreferencesRepository.getLastSelectedRole()
                            if (lastRole == "RESTAURANT") Screen.RestaurantHome.route else Screen.BuyerHome.route
                        }
                    }
                    Screen.EmailVerification.createRoute(rolDestino)
                }

                user.isBuyer && user.isOwner -> {
                    val lastRole = deps.userPreferencesRepository.getLastSelectedRole()
                    when (lastRole) {
                        "RESTAURANT" -> Screen.RestaurantHome.route
                        "BUYER" -> Screen.BuyerHome.route
                        else -> Screen.Login.route
                    }
                }
                user.isOwner -> Screen.RestaurantHome.route
                user.isBuyer -> Screen.BuyerHome.route
                else -> Screen.Login.route
            }
        } else {
            Screen.Login.route
        }
        uiState = NavUiState.Success(destino)
    }

    when (val state = uiState) {
        is NavUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFD32F2F))
            }
        }
        is NavUiState.Success -> {
            NavHost(navController = navController, startDestination = state.startDestination) {

                composable(Screen.Login.route) {
                    val vm: LoginViewModel = viewModel(factory = factory)
                    LoginScreen(
                        viewModel = vm,
                        onLoginSuccess = { rol ->
                            scope.launch {
                                checkSessionTrigger++
                                val destino =
                                    if (rol == "BUYER") Screen.BuyerHome.route else Screen.RestaurantHome.route
                                navController.navigate(destino) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        },
                        onNeedsEmailVerification = { rol ->
                            val destinoFinal =
                                if (rol == "BUYER") Screen.BuyerHome.route else Screen.RestaurantHome.route
                            navController.navigate(Screen.EmailVerification.createRoute(destinoFinal)) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onGoogleNewUser = {
                            navController.navigate(Screen.GoogleRoleChoice.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) },
                        onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
                    )
                }

                composable(Screen.ForgotPassword.route) {
                    val vm: ForgotPasswordViewModel = viewModel(factory = factory)
                    ForgotPasswordScreen(
                        viewModel = vm,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Onboarding.route) {
                    OnboardingScreen(
                        onBuyerSelected = { navController.navigate(Screen.RegisterBuyer.route) },
                        onRestaurantSelected = { navController.navigate(Screen.RegisterRestaurant.route) }
                    )
                }

                composable(Screen.GoogleRoleChoice.route) {
                    OnboardingScreen(
                        onBuyerSelected = {
                            navController.navigate(Screen.BuyerHome.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onRestaurantSelected = {
                            navController.navigate(Screen.CompleteRestaurantRegistration.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.RegisterBuyer.route) {
                    val vm: BuyerRegisterViewModel = viewModel(factory = factory)
                    BuyerRegisterScreen(
                        viewModel = vm,
                        onRegistrationComplete = {
                            navController.navigate(Screen.EmailVerification.createRoute(Screen.BuyerHome.route)) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.BuyerHome.route) {
                    val searchVm: BuyerSearchViewModel = viewModel(factory = factory)
                    val cartVm: CartViewModel = viewModel(factory = factory)
                    val trackingVm: OrderTrackingViewModel = viewModel(factory = factory)
                    BuyerDashboardScreen (
                        searchViewModel = searchVm,
                        cartViewModel = cartVm,
                        trackingViewModel = trackingVm,
                        onSwitchToRestaurantMode = {
                            scope.launch {
                                checkSessionTrigger++
                                navController.navigate(Screen.RestaurantHome.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        },
                        onAccountDeactivated = {
                            scope.launch {
                                checkSessionTrigger++
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        },
                        onLogout = {
                            scope.launch {
                                deps.userRepository.logout()
                                checkSessionTrigger++
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }
                composable("buyer_map_buyer") {
                    BuyerMapScreen(onNavigateBack = { navController.popBackStack() })
                }

                composable(Screen.RegisterRestaurant.route) {
                    val vm: RestaurantRegisterViewModel = viewModel(factory = factory)
                    RestaurantRegisterScreen(
                        viewModel = vm,
                        onRegistrationComplete = {
                            navController.navigate(Screen.EmailVerification.createRoute(Screen.RestaurantHome.route)) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.RestaurantHome.route) {
                    val vm: RestaurantDashboardViewModel = viewModel(factory = factory)
                    RestaurantDashboardScreen(
                        viewModel = vm,
                        onNoRestaurantFound = {
                            navController.navigate(Screen.CompleteRestaurantRegistration.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onSwitchToBuyerMode = {
                            scope.launch {
                                checkSessionTrigger++
                                navController.navigate(Screen.BuyerHome.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        },
                        onAccountDeactivated = {
                            scope.launch {
                                checkSessionTrigger++
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        },
                        onLogout = {
                            scope.launch {
                                deps.userRepository.logout()
                                checkSessionTrigger++
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable(Screen.CompleteRestaurantRegistration.route) {
                    val vm: CompleteRestaurantRegistrationViewModel = viewModel(factory = factory)
                    CompleteRestaurantRegistrationScreen (
                        viewModel = vm,
                        onComplete = {
                            scope.launch {
                                checkSessionTrigger++
                                navController.navigate(Screen.RestaurantHome.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        },
                        onExit = {
                            navController.navigate(Screen.GoogleRoleChoice.route){
                                popUpTo(0){ inclusive = true}
                            }
                        }
                    )
                }

                composable(
                    route = Screen.EmailVerification.route,
                    arguments = listOf(navArgument("next") { type = NavType.StringType })
                ) { backStackEntry ->
                    val vm: EmailVerificationViewModel = viewModel(factory = factory)
                    val destinoTrasVerificar = backStackEntry.arguments?.getString("next") ?: Screen.BuyerHome.route
                    EmailVerificationScreen(
                        viewModel = vm,
                        onVerified = {
                            scope.launch {
                                checkSessionTrigger++
                                navController.navigate(destinoTrasVerificar) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        },
                        onLoggedOut = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}