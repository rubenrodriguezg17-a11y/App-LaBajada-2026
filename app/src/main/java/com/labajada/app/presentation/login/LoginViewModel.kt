package com.labajada.app.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labajada.app.domain.repository.UserPreferencesRepository
import com.labajada.app.domain.repository.UserRepository
import com.labajada.app.domain.usecase.auth.LoginUseCase
import com.labajada.app.domain.usecase.auth.LoginWithGoogleUseCase
import com.labajada.app.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    private var pendingOnSuccess: ((String) -> Unit)? = null
    private var pendingOnNeedsEmailVerification: ((String) -> Unit)? = null

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, errorMessage = null) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, errorMessage = null) }

    fun login(onSuccess: (String) -> Unit, onNeedsEmailVerification: (String) -> Unit) {
        val state = _uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor, ingresa tu correo y contraseña.") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                val result = loginUseCase.execute(
                    email = state.email.trim(),
                    password = state.password
                )

                _uiState.update { it.copy(isLoading = false) }
                manejarResultadoLogin(result, onSuccess, onNeedsEmailVerification)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Error inesperado: ${e.localizedMessage}")
                }
            }
        }
    }

    fun loginWithGoogle(
        idToken: String,
        onExistingUser: (String) -> Unit,
        onNewUser: () -> Unit,
        onNeedsEmailVerification: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                val result = loginWithGoogleUseCase.execute(idToken)
                _uiState.update { it.copy(isLoading = false) }

                result.onSuccess { googleResult ->
                    if (googleResult.isNewUser) {
                        onNewUser()
                    } else {
                        manejarResultadoLogin(Result.success(googleResult.user), onExistingUser, onNeedsEmailVerification)
                    }
                }.onFailure { exception ->
                    _uiState.update { it.copy(errorMessage = exception.localizedMessage) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Error inesperado: ${e.localizedMessage}")
                }
            }
        }
    }
    fun onGoogleLoginError(mensaje: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = mensaje) }
    }

    private fun manejarResultadoLogin(
        result: Result<User>,
        onSuccess: (String) -> Unit,
        onNeedsEmailVerification: (String) -> Unit
    ) {
        result.onSuccess { user ->
            when {
                user.isBuyer && user.isOwner -> {
                    pendingOnSuccess = onSuccess
                    pendingOnNeedsEmailVerification = onNeedsEmailVerification
                    _uiState.update { it.copy(showRoleSelector = true) }
                }
                user.isOwner -> viewModelScope.launch { resolverAccesoPorRol("RESTAURANT", onSuccess, onNeedsEmailVerification) }
                user.isBuyer -> viewModelScope.launch { resolverAccesoPorRol("BUYER", onSuccess, onNeedsEmailVerification) }
                else -> _uiState.update {
                    it.copy(errorMessage = "Esta cuenta no tiene un rol asignado.")
                }
            }
        }.onFailure { exception ->
            _uiState.update { it.copy(errorMessage = exception.localizedMessage) }
        }
    }

    /**
     * Antes de dar acceso, confirma que el correo ya fue verificado. Las cuentas de Google
     * llegan aquí siempre verificadas (Firebase las marca así automáticamente), así que este
     * chequeo solo detiene a cuentas de correo/contraseña que nunca confirmaron el enlace.
     */
    private suspend fun resolverAccesoPorRol(
        role: String,
        onSuccess: (String) -> Unit,
        onNeedsEmailVerification: (String) -> Unit
    ) {
        _uiState.update { it.copy(isLoading = true) }
        val verificado = userRepository.isCurrentUserEmailVerified()
        _uiState.update { it.copy(isLoading = false) }
        if (verificado) onSuccess(role) else onNeedsEmailVerification(role)
    }

    fun selectRole(role: String) {
        _uiState.update { it.copy(showRoleSelector = false) }
        // Se capturan los callbacks en variables locales ANTES de limpiar los campos de la
        // clase: si se limpiaran primero y se leyeran recién dentro de la corrutina (que corre
        // async), una segunda llamada rápida a selectRole podría encontrarlos ya en null.
        val onSuccess = pendingOnSuccess
        val onNeedsEmailVerification = pendingOnNeedsEmailVerification
        pendingOnSuccess = null
        pendingOnNeedsEmailVerification = null

        if (onSuccess == null || onNeedsEmailVerification == null) return

        viewModelScope.launch {
            userPreferencesRepository.saveLastSelectedRole(role)
            resolverAccesoPorRol(role, onSuccess, onNeedsEmailVerification)
        }
    }

    fun dismissRoleSelector() {
        _uiState.update { it.copy(showRoleSelector = false) }
        pendingOnSuccess = null
        pendingOnNeedsEmailVerification = null
    }
}