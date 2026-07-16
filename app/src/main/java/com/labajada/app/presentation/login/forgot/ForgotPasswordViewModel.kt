package com.labajada.app.presentation.login.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labajada.app.domain.usecase.auth.SendPasswordResetEmailUseCase
import com.labajada.app.presentation.login.forgot.ForgotPasswordState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordState())
    val uiState: StateFlow<ForgotPasswordState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, error = null) }

    fun sendResetEmail() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = sendPasswordResetEmailUseCase(state.email)

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, emailSent = true) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, error = exception.localizedMessage ?: "No se pudo enviar el correo.")
                }
            }
        }
    }
}