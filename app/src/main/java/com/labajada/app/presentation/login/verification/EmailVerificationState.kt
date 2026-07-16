package com.labajada.app.presentation.login.verification

data class EmailVerificationState(
    val email: String = "",
    val isCheckingVerification: Boolean = false,
    val isResending: Boolean = false,
    val resendCooldownSeconds: Int = 0,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)
