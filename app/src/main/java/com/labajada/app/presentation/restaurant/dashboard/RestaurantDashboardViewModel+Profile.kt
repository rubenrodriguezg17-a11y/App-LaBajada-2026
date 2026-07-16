package com.labajada.app.presentation.restaurant.dashboard

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.labajada.app.domain.model.Restaurant
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * GESTIÓN DEL PERFIL DEL RESTAURANTE
 * Edición de datos del local, fotos de portada/documentos, apertura/cierre y guardado.
 */

fun RestaurantDashboardViewModel.toggleProfileEdit() =
    _uiState.update { it.copy(isEditingProfile = !_uiState.value.isEditingProfile) }

fun RestaurantDashboardViewModel.onProfileNameChange(v: String) = _uiState.update { it.copy(resNameByOwner = v) }
fun RestaurantDashboardViewModel.onProfileRucChange(v: String) = _uiState.update { it.copy(resRucByOwner = v) }
fun RestaurantDashboardViewModel.onProfilePhoneChange(v: String) = _uiState.update { it.copy(resPhoneByOwner = v) }
fun RestaurantDashboardViewModel.onProfileAddressChange(v: String) = _uiState.update { it.copy(resAddressByOwner = v) }
fun RestaurantDashboardViewModel.toggleProfileCategoryDropdown() =
    _uiState.update { it.copy(expandedProfileCategory = !it.expandedProfileCategory) }
fun RestaurantDashboardViewModel.onProfileCategorySelected(v: String) =
    _uiState.update { it.copy(resCategoryByOwner = v, expandedProfileCategory = false) }
fun RestaurantDashboardViewModel.toggleProfileMap(show: Boolean) = _uiState.update { it.copy(showProfileMapDialog = show) }
fun RestaurantDashboardViewModel.onProfileBusinessHoursChange(v: String) = _uiState.update { it.copy(resBusinessHours = v) }
fun RestaurantDashboardViewModel.onProfileOffersDeliveryChange(value: Boolean) =
    _uiState.update { it.copy(resOffersDelivery = value) }
fun RestaurantDashboardViewModel.onProfileMaxDeliveryDistanceChange(value: Double) =
    _uiState.update { it.copy(resMaxDeliveryDistanceKm = value) }

fun RestaurantDashboardViewModel.onProfileStorePhotoSelected(context: Context, uri: Uri?) {
    if (uri == null) {
        _uiState.update { it.copy(resStorePhotoUrl = null, resIsVerified = false) }
        guardarDatosDelLocal()
        return
    }
    val finalPath = if (uri.toString().startsWith("content://")) {
        guardarImagenEnAlmacenamientoInterno(context, uri) ?: return
    } else uri.toString()
    _uiState.update { it.copy(resStorePhotoUrl = finalPath, resIsVerified = false) }
    guardarDatosDelLocal()
}

fun RestaurantDashboardViewModel.onProfileMenuPhotoSelected(context: Context, uri: Uri?) {
    if (uri == null) {
        _uiState.update { it.copy(resMenuPhotoUrl = null) }
        guardarDatosDelLocal()
        return
    }
    val finalPath = if (uri.toString().startsWith("content://")) {
        guardarImagenEnAlmacenamientoInterno(context, uri) ?: return
    } else uri.toString()
    _uiState.update { it.copy(resMenuPhotoUrl = finalPath) }
    guardarDatosDelLocal()
}

fun RestaurantDashboardViewModel.onProfilePermitPhotoSelected(context: Context, uri: Uri?) {
    if (uri == null) {
        _uiState.update { it.copy(resPermitPhotoUrl = null) }
        guardarDatosDelLocal()
        return
    }
    val finalPath = if (uri.toString().startsWith("content://")) {
        guardarImagenEnAlmacenamientoInterno(context, uri) ?: return
    } else uri.toString()
    _uiState.update { it.copy(resPermitPhotoUrl = finalPath) }
    guardarDatosDelLocal()
}

fun RestaurantDashboardViewModel.enviarDocumentos() {
    _uiState.update { it.copy(resDocumentsSubmittedAt = System.currentTimeMillis()) }
    guardarDatosDelLocal()
}

fun RestaurantDashboardViewModel.onProfileLocationConfirmed(lat: Double, lng: Double) = _uiState.update {
    it.copy(resLatitude = lat, resLongitude = lng, showProfileMapDialog = false)
}

fun RestaurantDashboardViewModel.onProfileImageSelected(context: Context, uri: Uri?) {
    if (uri == null) return
    val finalPath = if (uri.toString().startsWith("content://")) {
        guardarImagenEnAlmacenamientoInterno(context, uri) ?: return
    } else {
        uri.toString()
    }
    _uiState.update { it.copy(resImageUrl = finalPath) }
    guardarDatosDelLocal()
}

fun RestaurantDashboardViewModel.onProfileImageCropped(context: Context, bitmap: android.graphics.Bitmap) {
    val fileName = "portada_${System.currentTimeMillis()}.jpg"
    val finalPath = com.labajada.app.core.utils.saveBitmapToInternalStorage(context, bitmap, fileName)
        ?: return
    _uiState.update { it.copy(resImageUrl = finalPath) }
    guardarDatosDelLocal()
}
fun RestaurantDashboardViewModel.toggleIsOpen() {
    val newValue = !_uiState.value.resIsOpen
    _uiState.update { it.copy(resIsOpen = newValue) }
    viewModelScope.launch {
        val state = _uiState.value
        val restauranteActualizado = Restaurant(
            id = currentRestaurantId,
            ownerId = currentOwnerId,
            restaurantName = state.resNameByOwner,
            documentType = state.resDocumentType,
            documentNumber = state.resRucByOwner,
            phoneNumber = state.resPhoneByOwner,
            selectedCategory = state.resCategoryByOwner,
            addressDetails = state.resAddressByOwner,
            latitude = state.resLatitude,
            longitude = state.resLongitude,
            offersDelivery = state.resOffersDelivery,
            maxDeliveryDistanceKm = state.resMaxDeliveryDistanceKm,
            imageUrl = state.resImageUrl,
            isOpen = state.resIsOpen,
            businessHours = state.resBusinessHours.ifBlank { null },
            storePhotoUrl = state.resStorePhotoUrl,
            menuPhotoUrl = state.resMenuPhotoUrl,
            permitPhotoUrl = state.resPermitPhotoUrl,
            isVerified = state.resIsVerified,
            documentsSubmittedAt = state.resDocumentsSubmittedAt,
        )
        restaurantRepository.updateRestaurantProfile(restauranteActualizado)
    }
}

fun RestaurantDashboardViewModel.guardarDatosDelLocal() {
    val state = _uiState.value
    viewModelScope.launch {
        val restauranteActualizado = Restaurant(
            id = currentRestaurantId,
            ownerId = currentOwnerId,
            restaurantName = state.resNameByOwner,
            documentType = state.resDocumentType,
            documentNumber = state.resRucByOwner,
            phoneNumber = state.resPhoneByOwner,
            selectedCategory = state.resCategoryByOwner,
            addressDetails = state.resAddressByOwner,
            latitude = state.resLatitude,
            longitude = state.resLongitude,
            offersDelivery = state.resOffersDelivery,
            maxDeliveryDistanceKm = state.resMaxDeliveryDistanceKm,
            imageUrl = state.resImageUrl,
            isOpen = state.resIsOpen,
            businessHours = state.resBusinessHours.ifBlank { null },
            storePhotoUrl = state.resStorePhotoUrl,
            menuPhotoUrl = state.resMenuPhotoUrl,
            permitPhotoUrl = state.resPermitPhotoUrl,
            isVerified = state.resIsVerified,
            documentsSubmittedAt = state.resDocumentsSubmittedAt
        )
        restaurantRepository.updateRestaurantProfile(restauranteActualizado)
        _uiState.update { it.copy(isEditingProfile = false) }
    }
}
