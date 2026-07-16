package com.labajada.app.presentation.buyer.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labajada.app.core.validation.PasswordValidator
import com.labajada.app.core.validation.PeruValidators
import com.labajada.app.domain.usecase.auth.RegisterBuyerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BuyerRegisterViewModel(
    private val registerBuyerUseCase: RegisterBuyerUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BuyerRegisterState())
    val uiState: StateFlow<BuyerRegisterState> = _uiState.asStateFlow()

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, error = null) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value, error = null) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, error = null) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, error = null) }
    fun onAcceptedTermsChange(value: Boolean) = _uiState.update { it.copy(acceptedTerms = value, error = null) }
    fun registerBuyer(onComplete: () -> Unit) {
        val state = _uiState.value

        if (!state.acceptedTerms) {
            _uiState.update { it.copy(error = "Debes aceptar los Términos y Condiciones para continuar.") }
            return
        }

        if (state.name.isBlank() || state.phone.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Por favor, completa todos los campos obligatorios.") }
            return
        }
        if (!PeruValidators.isValidPhone(state.phone)) {
            _uiState.update { it.copy(error = "Ingresa un celular válido (9 dígitos, empieza con 9).") }
            return
        }
        if (!PasswordValidator.isValidEmail(state.email)) {
            _uiState.update { it.copy(error = "Ingresa un correo electrónico válido.") }
            return
        }
        val passwordCheck = PasswordValidator.validate(state.password)
        if (!passwordCheck.isValid) {
            _uiState.update { it.copy(error = "La contraseña no cumple con los requisitos de seguridad.") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val result = registerBuyerUseCase.execute(
                    email = state.email.trim(),
                    password = state.password,
                    fullName = state.name.trim(),
                    phoneNumber = state.phone.trim()
                )

                _uiState.update { it.copy(isLoading = false) }

                result.onSuccess {
                    onComplete()
                }.onFailure { exception ->
                    _uiState.update { it.copy(error = "Fallo en el registro: ${exception.localizedMessage}") }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Error inesperado: ${e.localizedMessage}")
                }
            }
        }
    }
}