package com.labajada.app.presentation.restaurant.register.completeRestaurantRegistration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labajada.app.core.validation.PeruValidators
import com.labajada.app.domain.model.Restaurant
import com.labajada.app.domain.usecase.restaurant.CompleteRestaurantRegistrationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CompleteRestaurantRegistrationViewModel(
    private val completeRestaurantRegistrationUseCase: CompleteRestaurantRegistrationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompleteRestaurantRegistrationState())
    val uiState: StateFlow<CompleteRestaurantRegistrationState> = _uiState.asStateFlow()

    fun nextStep() {
        val state = _uiState.value
        val canAdvance = when (state.currentStep) {
            1 -> validateBusinessInfo()
            2 -> validateLocation()
            else -> true
        }
        if (canAdvance) {
            _uiState.update { it.copy(currentStep = it.currentStep + 1, error = null) }
        }
    }

    fun previousStep() {
        _uiState.update {
            if (it.currentStep > 1) it.copy(currentStep = it.currentStep - 1, error = null) else it
        }
    }

    private fun validateBusinessInfo(): Boolean {
        val state = _uiState.value
        if (state.restaurantName.isBlank() || state.documentNumber.isBlank() ||
            state.phoneNumber.isBlank() || state.selectedCategory.isBlank()) {
            _uiState.update { it.copy(error = "Completa todos los campos.") }
            return false
        }
        val documentoValido = if (state.documentType == "RUC")
            PeruValidators.isValidRuc(state.documentNumber)
        else
            PeruValidators.isValidDni(state.documentNumber)

        if (!documentoValido) {
            _uiState.update { it.copy(error = "El ${state.documentType} ingresado no es válido.") }
            return false
        }
        if (!PeruValidators.isValidPhone(state.phoneNumber)) {
            _uiState.update { it.copy(error = "Ingresa un celular válido (9 dígitos, empieza con 9).") }
            return false
        }
        return true
    }

    private fun validateLocation(): Boolean {
        val state = _uiState.value
        if (state.addressDetails.isBlank() || !state.isLocationSelected) {
            _uiState.update { it.copy(error = "Completa la dirección y selecciona ubicación en el mapa.") }
            return false
        }
        return true
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(restaurantName = value, error = null) }
    fun onDocumentTypeChange(value: String) = _uiState.update { it.copy(documentType = value, documentNumber = "", error = null) }
    fun onDocumentNumberChange(value: String) = _uiState.update { it.copy(documentNumber = value, error = null) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phoneNumber = value, error = null) }
    fun onCategorySelected(category: String) = _uiState.update { it.copy(selectedCategory = category, expandedCategory = false) }
    fun toggleCategoryDropdown() = _uiState.update { it.copy(expandedCategory = !it.expandedCategory) }

    fun onAddressChange(value: String) = _uiState.update { it.copy(addressDetails = value, error = null) }
    fun toggleMapDialog(show: Boolean) = _uiState.update { it.copy(showMapDialog = show) }
    fun onLocationConfirmed(lat: Double, lng: Double) = _uiState.update {
        it.copy(latitude = lat, longitude = lng, isLocationSelected = true, showMapDialog = false)
    }
    fun onOffersDeliveryChange(value: Boolean) = _uiState.update { it.copy(offersDelivery = value) }
    fun onMaxDeliveryDistanceChange(value: Double) = _uiState.update { it.copy(maxDeliveryDistanceKm = value) }
    fun onImageSelected(url: String?) = _uiState.update { it.copy(imageUrl = url) }
    fun onBusinessHoursChange(value: String) = _uiState.update { it.copy(businessHours = value) }

    fun onStorePhotoSelected(url: String?) = _uiState.update { it.copy(storePhotoUrl = url, error = null) }
    fun onMenuPhotoSelected(url: String?) = _uiState.update { it.copy(menuPhotoUrl = url) }
    fun onPermitPhotoSelected(url: String?) = _uiState.update { it.copy(permitPhotoUrl = url) }
    fun onAcceptedTermsChange(value: Boolean) = _uiState.update { it.copy(acceptedTerms = value, error = null) }

    fun completeRegistration(onComplete: () -> Unit) {
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val restaurantData = Restaurant(
                ownerId = "",  // se completa dentro del UseCase
                restaurantName = state.restaurantName.trim(),
                documentType = state.documentType,
                documentNumber = state.documentNumber.trim(),
                phoneNumber = state.phoneNumber.trim(),
                selectedCategory = state.selectedCategory,
                addressDetails = state.addressDetails.trim(),
                latitude = state.latitude,
                longitude = state.longitude,
                offersDelivery = state.offersDelivery,
                maxDeliveryDistanceKm = if (state.offersDelivery) state.maxDeliveryDistanceKm else 0.0,
                imageUrl = state.imageUrl,
                businessHours = state.businessHours?.trim()?.ifBlank { null },
                storePhotoUrl = state.storePhotoUrl,
                menuPhotoUrl = state.menuPhotoUrl,
                permitPhotoUrl = state.permitPhotoUrl,
                isVerified = false
            )

            val result = completeRestaurantRegistrationUseCase.execute(restaurantData)
            _uiState.update { it.copy(isLoading = false) }

            result.onSuccess {
                onComplete()
            }.onFailure { exception ->
                _uiState.update { it.copy(error = "Error: ${exception.localizedMessage}") }
            }
        }
    }
}