package com.labajada.app.data.mapper

import com.labajada.app.data.local.entity.SessionEntity
import com.labajada.app.domain.model.Session

fun Session.toEntity() = SessionEntity(
    id = id,
    userId = userId,
    email = email,
    role = role
)

fun SessionEntity.toDomain() = Session(
    id = id,
    userId = userId,
    email = email,
    role = role
)