package com.labajada.app.presentation.restaurant.register

data class RestaurantRegisterState(
    val currentStep: Int = 1,

    // Paso 1: Identidad del negocio
    val restaurantName: String = "",
    val documentType: String = "DNI", // "DNI" o "RUC"
    val documentNumber: String = "",
    val phoneNumber: String = "",
    val selectedCategory: String = "",
    val expandedCategory: Boolean = false,

    // Paso 2: Ubicación y delivery
    val addressDetails: String = "",
    val latitude: Double = -8.1116,
    val longitude: Double = -79.0287,
    val isLocationSelected: Boolean = false,
    val showMapDialog: Boolean = false,
    val offersDelivery: Boolean = false,
    val maxDeliveryDistanceKm: Double = 0.1,
    val imageUrl: String? = null,
    val businessHours: String? = null,
    val ownerFullName: String = "",

    // Paso 3: Documentos y fotos
    val storePhotoUrl: String? = null,
    val menuPhotoUrl: String? = null,
    val permitPhotoUrl: String? = null,

    // Paso 4: Credenciales
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    // Estado general
    val isLoading: Boolean = false,
    val error: String? = null,

    // terminos y condiciones
    val acceptedTerms: Boolean = false,
) {
    val isStep1Valid: Boolean
        get() = restaurantName.isNotBlank() && documentNumber.isNotBlank() &&
                phoneNumber.isNotBlank() && selectedCategory.isNotBlank()

    val isStep2Valid: Boolean
        get() = addressDetails.isNotBlank() && isLocationSelected &&
                (!offersDelivery || maxDeliveryDistanceKm > 0)

    val isStep3Valid: Boolean
        get() = true

    val isStep4Valid: Boolean
        get() = email.isNotBlank() && password.isNotBlank() &&
                confirmPassword.isNotBlank() && password == confirmPassword
}