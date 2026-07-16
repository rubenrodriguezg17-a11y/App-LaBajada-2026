package com.labajada.app.presentation.restaurant.dashboard.components.modals

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

@SuppressLint("MissingPermission")
@Composable
fun RestaurantMapDialog(
    initialLatitude: Double,
    initialLongitude: Double,
    onConfirm: (Double, Double) -> Unit,
    onDismiss: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // Estado para controlar si el permiso de ubicación fue concedido
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Lanzador para solicitar el permiso dinámicamente si no está concedido
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    val initialPosition = remember(initialLatitude, initialLongitude) {
        LatLng(initialLatitude, initialLongitude)
    }
    val markerState = rememberMarkerState(position = initialPosition)
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 17f)
    }

    // Efecto para solicitar permisos e intentar obtener la ubicación actual
    LaunchedEffect(hasLocationPermission) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        markerState.position = currentLatLng
                        cameraState.position = CameraPosition.fromLatLngZoom(currentLatLng, 16f)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("RestaurantMapDialog", "Error al obtener ubicacion actual", e)
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
                .fillMaxHeight(0.75f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ubica tu local (Autocentrado activo)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238)
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraState,
                        onMapClick = { latLng -> markerState.position = latLng },
                        // Modificado: Ahora solo se activa si el permiso fue concedido
                        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                        uiSettings = MapUiSettings(
                            myLocationButtonEnabled = hasLocationPermission,
                            rotationGesturesEnabled = false,
                            scrollGesturesEnabled = true,
                        )
                    ) {
                        Marker(
                            state = markerState,
                            title = "Mi Ubicación Actual",
                            draggable = true
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancelar", color = Color.Gray)
                    }
                    Button(
                        onClick = {
                            onConfirm(markerState.position.latitude, markerState.position.longitude)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Confirmar Coordenadas", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}