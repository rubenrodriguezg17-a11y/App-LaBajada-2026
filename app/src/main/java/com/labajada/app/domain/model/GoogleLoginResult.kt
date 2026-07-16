package com.labajada.app.domain.model

data class GoogleLoginResult(
    val user: User,
    val isNewUser: Boolean
)
