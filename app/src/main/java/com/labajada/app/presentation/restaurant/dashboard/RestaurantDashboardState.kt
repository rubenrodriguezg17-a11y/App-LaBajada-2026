package com.labajada.app.presentation.restaurant.dashboard

import android.net.Uri

data class RestaurantDashboardState(
    // Navegación BottomNav
    val selectedTab: Int = 0,

    // Modales
    val showDeleteDialog: Boolean = false,
    val showFormSheet: Boolean = false,

    // Formulario de Platillos
    val dishName: String = "",
    val dishPrice: String = "",
    val selectedImageUri: Uri? = null,
    val isEditing: Boolean = false,
    val itemIndexToAction: Int? = null,
    val itemIdToEdit: String = "",

    // Perfil del Local
    val isEditingProfile: Boolean = false,
    val resNameByOwner: String = "",
    val resDocumentType: String = "DNI",
    val resRucByOwner: String = "",
    val resPhoneByOwner: String = "",
    val resAddressByOwner: String = "",
    val resCategoryByOwner: String = "",
    val resLatitude: Double = -8.1116,
    val resLongitude: Double = -79.0287,
    val showProfileMapDialog: Boolean = false,
    val expandedProfileCategory: Boolean = false,
    val resOffersDelivery: Boolean = false,
    val resMaxDeliveryDistanceKm: Double = 3.0,
    val resImageUrl: String? = null,
    val resIsOpen: Boolean = false,
    val resBusinessHours: String = "",

    // Documentos de verificación
    val resStorePhotoUrl: String? = null,
    val resMenuPhotoUrl: String? = null,
    val resPermitPhotoUrl: String? = null,
    val resIsVerified: Boolean = false,
    val resDocumentsSubmittedAt: Long? = null,

    // Privacidad financiera
    val isGananciasVisible: Boolean = false,

    // Configuracion de perfil
    val showConfigSection: Boolean = false
)