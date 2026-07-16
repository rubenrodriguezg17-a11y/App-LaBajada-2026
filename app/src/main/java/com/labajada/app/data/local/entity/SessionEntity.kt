package com.labajada.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_session")
data class SessionEntity(
    @PrimaryKey val id: Int = 1,
    val userId: String,
    val email: String,
    val role: String
)
