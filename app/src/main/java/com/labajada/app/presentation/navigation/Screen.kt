package com.labajada.app.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Onboarding : Screen("onboarding")
    object GoogleRoleChoice : Screen("google_role_choice")
    object RegisterBuyer : Screen("register_buyer")
    object RegisterRestaurant : Screen("register_restaurant")
    object BuyerHome : Screen("buyer_home")
    object RestaurantHome : Screen("restaurant_home")
    object ForgotPassword : Screen("forgot_password")
    object CompleteRestaurantRegistration : Screen("complete_restaurant_registration")

    /**
     * Ruta con argumento `next`: a dónde navegar una vez que el usuario confirme su correo
     * (Screen.BuyerHome.route o Screen.RestaurantHome.route). Se usa createRoute() para
     * construirla con el destino correcto según de dónde venga (login o registro).
     */
    object EmailVerification : Screen("email_verification/{next}") {
        fun createRoute(next: String) = "email_verification/$next"
    }
}
