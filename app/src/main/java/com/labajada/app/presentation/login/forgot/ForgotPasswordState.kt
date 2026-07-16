package com.labajada.app.presentation.login.forgot

import coil.compose.AsyncImagePainter

data class ForgotPasswordState (
    val email: String="",
    val isLoading: Boolean= false,
    val error: String? = null,
    val emailSent: Boolean = false
)
