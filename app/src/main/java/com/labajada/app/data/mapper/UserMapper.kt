package com.labajada.app.data.mapper

import com.labajada.app.data.local.entity.UserEntity
import com.labajada.app.domain.model.User

fun User.toEntity() = UserEntity(
    uid = uid,
    email = email,
    fullName = fullName,
    phoneNumber = phoneNumber,
    isBuyer = isBuyer,
    isOwner = isOwner,
    isActive = isActive
)

fun UserEntity.toDomain() = User(
    uid = uid,
    email = email,
    fullName = fullName,
    phoneNumber = phoneNumber,
    isBuyer = isBuyer,
    isOwner = isOwner,
    isActive = isActive

)