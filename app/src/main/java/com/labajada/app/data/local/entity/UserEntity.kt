package com.labajada.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val uid: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val isBuyer: Boolean = false,
    val isOwner: Boolean = false,
    val isActive: Boolean = true
)