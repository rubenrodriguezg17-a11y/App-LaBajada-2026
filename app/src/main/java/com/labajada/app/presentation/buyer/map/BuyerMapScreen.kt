package com.labajada.app.presentation.buyer.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class HuariqueSimulado(
    val id: String,
    val nombre: String,
    val categoria: String,
    val rating: String,
    val latitud: Double,
    val longitud: Double,
    val imagen: String
)

@Composable
fun BuyerMapScreen(
    onNavigateBack: () -> Unit
) {

    val scope = rememberCoroutineScope()
    val huariquesDePrueba = remember {
        listOf(
            HuariqueSimulado("1", "El Cevichazo Fino", "🐟 Cevichería", "⭐️ 4.8", -8.1116, -79.0287, ""),
            HuariqueSimulado("2", "La Bajada Criolla", "🍲 Criollo", "⭐️ 4.6", -8.1150, -79.0320, ""),
            HuariqueSimulado("3", "Hamburguesas El Tío Toque", "🍔 Bajadas", "⭐️ 4.9", -8.1090, -79.0250, "")
        )
    }

    // Ubicación inicial de la cámara en el mapa (Trujillo)
    val ubicacionInicial = LatLng(-8.1116, -79.0287)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ubicacionInicial, 14.5f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false),
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        ) {
            huariquesDePrueba.forEach { huarique ->
                Marker(
                    state = rememberMarkerState(position = LatLng(huarique.latitud, huarique.longitud)),
                    title = huarique.nombre,
                    snippet = "${huarique.categoria} • ${huarique.rating}",
                    onClick = {
                        scope.launch {
                                cameraPositionState.animate(
                                com.google.android.gms.maps.CameraUpdateFactory.newLatLng(
                                    LatLng(huarique.latitud, huarique.longitud)
                                )
                            )
                        }
                        false
                    }
                )
            }
        }
         // boton para regresar atras
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .padding(top = 48.dp, start = 16.dp)
                .background(Color.White, shape = RoundedCornerShape(50.dp))
                .size(44.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color(0xFF212121))
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp), // Separación estética del borde de la pantalla
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(huariquesDePrueba, key = { it.id }) { huarique ->
                Card(
                    modifier = Modifier
                        .width(290.dp)
                        .height(110.dp)
                        .clickable {
                            // Si el cliente toca la tarjeta, el mapa se desliza con suavidad hacia ese local
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                LatLng(huarique.latitud, huarique.longitud), 16f
                            )
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Espacio reservado para la foto del restaurante (Usa Coil)
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .background(Color(0xFFF0F0F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (huarique.imagen.isNotEmpty()) {
                                AsyncImage(
                                    model = huarique.imagen, contentDescription = null,
                                    modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(32.dp))
                            }
                        }

                        // Información textual de la tarjeta
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = huarique.nombre, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF212121), maxLines = 1)
                                Text(text = huarique.categoria, fontSize = 13.sp, color = Color(0xFF757575))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = huarique.rating, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                                Text(text = "Ver Menú", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                            }
                        }
                    }
                }
            }
        }
    }
}