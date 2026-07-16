package com.labajada.app.data.mapper

import com.labajada.app.data.local.entity.RestaurantEntity
import com.labajada.app.domain.model.Restaurant

fun Restaurant.toEntity() = RestaurantEntity(
    id = id,
    ownerId = ownerId,
    restaurantName = restaurantName,
    documentType = documentType,
    documentNumber = documentNumber,
    phoneNumber = phoneNumber,
    selectedCategory = selectedCategory,
    addressDetails = addressDetails,
    latitude = latitude,
    longitude = longitude,
    offersDelivery = offersDelivery,
    maxDeliveryDistanceKm = maxDeliveryDistanceKm,
    imageUrl = imageUrl,
    isOpen = isOpen,
    businessHours = businessHours,
    isActive = isActive,
    storePhotoUrl = storePhotoUrl,
    menuPhotoUrl = menuPhotoUrl,
    permitPhotoUrl = permitPhotoUrl,
    isVerified = isVerified,
    documentsSubmittedAt = documentsSubmittedAt
)

fun RestaurantEntity.toDomain() = Restaurant(
    id = id,
    ownerId = ownerId,
    restaurantName = restaurantName,
    documentType = documentType,
    documentNumber = documentNumber,
    phoneNumber = phoneNumber,
    selectedCategory = selectedCategory,
    addressDetails = addressDetails,
    latitude = latitude,
    longitude = longitude,
    offersDelivery = offersDelivery,
    maxDeliveryDistanceKm = maxDeliveryDistanceKm,
    imageUrl = imageUrl,
    isOpen = isOpen,
    businessHours = businessHours,
    isActive = isActive,
    storePhotoUrl = storePhotoUrl,
    menuPhotoUrl = menuPhotoUrl,
    permitPhotoUrl = permitPhotoUrl,
    isVerified = isVerified,
    documentsSubmittedAt = documentsSubmittedAt
)