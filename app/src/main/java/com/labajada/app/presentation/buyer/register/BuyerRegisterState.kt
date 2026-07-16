package com.labajada.app.presentation.buyer.register

data class BuyerRegisterState(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val acceptedTerms: Boolean = false,
)