package com.labajada.app.presentation.restaurant.register.completeRestaurantRegistration

data class CompleteRestaurantRegistrationState(
    val currentStep: Int = 1,

    val restaurantName: String = "",
    val documentType: String = "DNI",
    val documentNumber: String = "",
    val phoneNumber: String = "",
    val selectedCategory: String = "",
    val expandedCategory: Boolean = false,

    val addressDetails: String = "",
    val latitude: Double = -8.1116,
    val longitude: Double = -79.0287,
    val isLocationSelected: Boolean = false,
    val showMapDialog: Boolean = false,
    val offersDelivery: Boolean = false,
    val maxDeliveryDistanceKm: Double = 0.1,
    val imageUrl: String? = null,
    val businessHours: String? = null,

    // Paso 3: Documentos y fotos
    val storePhotoUrl: String? = null,
    val menuPhotoUrl: String? = null,
    val permitPhotoUrl: String? = null,
    val acceptedTerms: Boolean = false,

    val isLoading: Boolean = false,
    val error: String? = null
)