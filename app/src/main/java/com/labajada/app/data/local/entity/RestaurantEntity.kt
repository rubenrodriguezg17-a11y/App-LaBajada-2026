package com.labajada.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey
    val id: String,
    val ownerId: String,
    val restaurantName: String,
    val documentType: String = "DNI",
    val documentNumber: String,
    val phoneNumber: String,
    val selectedCategory: String,
    val addressDetails: String,
    val latitude: Double,
    val longitude: Double,
    val offersDelivery: Boolean,
    val maxDeliveryDistanceKm: Double,
    val imageUrl: String? = null,
    val isOpen: Boolean = false,
    val businessHours: String? = null,
    val isActive: Boolean = true,
    val storePhotoUrl: String? = null,
    val menuPhotoUrl: String? = null,
    val permitPhotoUrl: String? = null,
    val isVerified: Boolean = false,
    val documentsSubmittedAt: Long? = null
)