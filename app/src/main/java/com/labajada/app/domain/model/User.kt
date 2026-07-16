package com.labajada.app.domain.model

data class User(
    val uid: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val isBuyer: Boolean = false,
    val isOwner: Boolean = false,
    val isActive: Boolean = true
)