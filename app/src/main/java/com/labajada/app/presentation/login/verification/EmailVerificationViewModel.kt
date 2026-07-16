package com.labajada.app.presentation.login.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labajada.app.domain.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val RESEND_COOLDOWN_SECONDS = 30

class EmailVerificationViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailVerificationState())
    val uiState: StateFlow<EmailVerificationState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val session = userRepository.getActiveSession()
            _uiState.update { it.copy(email = session?.email ?: "") }
        }
    }

    /**
     * Recarga el usuario desde Firebase y revisa si ya confirmó el enlace del correo.
     * Si sí, ejecuta onVerified() (quien la llama decide a dónde navegar: BuyerHome o
     * RestaurantHome). Si no, muestra un aviso en vez de dejarlo pasar.
     */
    fun checkVerification(onVerified: () -> Unit) {
        if (_uiState.value.isCheckingVerification) return
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingVerification = true, errorMessage = null, infoMessage = null) }
            val verificado = userRepository.isCurrentUserEmailVerified()
            _uiState.update { it.copy(isCheckingVerification = false) }
            if (verificado) {
                onVerified()
            } else {
                _uiState.update {
                    it.copy(errorMessage = "Aún no confirmamos tu correo. Revisa tu bandeja de entrada (y spam) y toca el enlace que te enviamos.")
                }
            }
        }
    }

    fun resendEmail() {
        val state = _uiState.value
        if (state.isResending || state.resendCooldownSeconds > 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isResending = true, errorMessage = null, infoMessage = null) }
            val result = userRepository.sendEmailVerification()
            _uiState.update { it.copy(isResending = false) }

            result.onSuccess {
                _uiState.update { it.copy(infoMessage = "Te reenviamos el correo de verificación.") }
                iniciarCooldown()
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message ?: "No se pudo reenviar el correo. Intenta de nuevo.") }
            }
        }
    }

    private fun iniciarCooldown() {
        viewModelScope.launch {
            for (segundosRestantes in RESEND_COOLDOWN_SECONDS downTo 0) {
                _uiState.update { it.copy(resendCooldownSeconds = segundosRestantes) }
                delay(1000)
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            userRepository.logout()
            onLoggedOut()
        }
    }
}
