package com.labajada.app.domain.model

data class Session(
    val id: Int = 1,
    val userId: String,
    val email: String,
    val role: String
)