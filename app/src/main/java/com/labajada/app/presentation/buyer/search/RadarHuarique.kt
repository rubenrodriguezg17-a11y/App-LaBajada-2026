package com.labajada.app.presentation.buyer.search

data class RadarHuarique(
    val id: String,
    val nombre: String,
    val category: String,
    val precioPromedio: Double,
    val distancia: String,
    val latitud: Double,
    val longitud: Double,
    val isOpen: Boolean = true,
    val maxDeliveryDistanceKm: Double = 0.0,
    val offersDelivery: Boolean = false,
    val imageUrl: String? = null,
    val documentType: String = "DNI",
    val isVerified: Boolean = false,
    val documentsSubmittedAt: Long? = null,
    val storePhotoUrl: String? = null,
    val menuPhotoUrl: String? = null,
    val permitPhotoUrl: String? = null
)