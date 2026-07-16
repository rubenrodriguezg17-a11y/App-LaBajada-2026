package com.labajada.app.presentation.restaurant.register.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlin.math.roundToInt

@Composable
fun DeliveryRadiusMapDialog(
    initialLatitude: Double,
    initialLongitude: Double,
    initialOffersDelivery: Boolean,
    initialMaxDeliveryDistanceKm: Double,
    onDismiss: () -> Unit,
    onConfirm: (lat: Double, lng: Double, offersDelivery: Boolean, radiusKm: Double) -> Unit
) {
    val context = LocalContext.current

    // Si no hay ubicación previa guardada, usamos el punto por defecto temporalmente
    // mientras se resuelve el GPS real
    val tienePosicionPrevia = initialLatitude != -8.1116 || initialLongitude != -79.0287
    val puntoInicial = remember {
        LatLng(initialLatitude, initialLongitude)
    }
    val markerState = rememberMarkerState(position = puntoInicial)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(puntoInicial, 16f)
    }

    var offersDelivery by remember { mutableStateOf(initialOffersDelivery) }
    var radiusMeters by remember {
        mutableFloatStateOf((initialMaxDeliveryDistanceKm * 1000).toFloat().coerceIn(100f, 10000f))
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    // Si el restaurante no tenía ubicación previa, pedimos permisos y centramos en el GPS real
    LaunchedEffect(Unit) {
        if (!tienePosicionPrevia) {
            if (!hasLocationPermission) {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission && !tienePosicionPrevia) {
            try {
                @SuppressLint("MissingPermission")
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val miUbicacion = LatLng(location.latitude, location.longitude)
                        markerState.position = miUbicacion
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(miUbicacion, 17f)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("DeliveryRadiusMapDialog", "Error al obtener ubicacion actual", e)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Toca el mapa o arrastra el marcador 📍",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238)
                    )
                }

                // --- MAPA ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                        uiSettings = MapUiSettings(myLocationButtonEnabled = hasLocationPermission),
                        onMapClick = { latLng -> markerState.position = latLng }
                    ) {
                        Marker(
                            state = markerState,
                            title = "Ubicación de mi Huarique",
                            snippet = "Arrástrame hasta tu puerta",
                            draggable = true
                        )

                        if (offersDelivery) {
                            Circle(
                                center = markerState.position,
                                radius = radiusMeters.toDouble(),
                                fillColor = Color(0x332196F3),
                                strokeColor = Color(0xFF2196F3),
                                strokeWidth = 2f
                            )
                        }
                    }
                }

                // --- CONTROLES DE DELIVERY ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "¿Tu negocio hace delivery?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DeliveryOptionChip(
                            label = "Sí hace Delivery",
                            selected = offersDelivery,
                            onClick = { offersDelivery = true },
                            modifier = Modifier.weight(1f)
                        )
                        DeliveryOptionChip(
                            label = "Solo Recojo en Local",
                            selected = !offersDelivery,
                            onClick = { offersDelivery = false },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (offersDelivery) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Distancia máxima de reparto",
                                fontSize = 13.sp,
                                color = Color(0xFF757575)
                            )
                            Text(
                                text = formatearDistancia(radiusMeters),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3)
                            )
                        }
                        Slider(
                            value = radiusMeters,
                            onValueChange = { nuevoValor ->
                                // Redondea a pasos de 50m para que el usuario sienta control fino
                                radiusMeters = (nuevoValor / 100f).roundToInt() * 100f
                            },
                            valueRange = 50f..5000f,  // 50m a 5km
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF2196F3),
                                activeTrackColor = Color(0xFF2196F3)
                            )
                        )
                    }
                }

                // --- ACCIONES ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancelar", color = Color(0xFF757575), fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = {
                            onConfirm(
                                markerState.position.latitude,
                                markerState.position.longitude,
                                offersDelivery,
                                if (offersDelivery) (radiusMeters / 1000.0) else 0.0
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirmar", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

fun formatearDistancia(metros: Float): String {
    return if (metros < 1000f) {
        "${metros.toInt()} m"
    } else {
        val km = metros / 1000f
        if (km == km.toInt().toFloat()) "${km.toInt()} km" else String.format("%.1f km", km)
    }
}

@Composable
private fun DeliveryOptionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF263238),
            selectedLabelColor = Color.White
        )
    )
}