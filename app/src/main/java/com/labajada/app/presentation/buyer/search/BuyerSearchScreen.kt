package com.labajada.app.presentation.buyer.search

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.labajada.app.core.extensions.toPrecioDouble
import com.labajada.app.presentation.buyer.cart.CartFlightBus
import com.labajada.app.presentation.buyer.cart.CartViewModel
import com.labajada.app.presentation.buyer.search.components.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerSearchScreen(
    searchViewModel: BuyerSearchViewModel,
    cartViewModel: CartViewModel
) {
    val context = LocalContext.current
    val platosEncontrados by searchViewModel.platosEncontrados.collectAsState()
    val huariquesRadar by searchViewModel.huariquesDesdeBaseDeDatos.collectAsState()
    val ultimasBusquedas by searchViewModel.searchHistory.collectAsState()
    val queryText by searchViewModel.searchQuery.collectAsState()
    val cartItemCount by cartViewModel.itemCount.collectAsState()
    val menuDelHuarique by searchViewModel.menuDelHuariqueSeleccionado.collectAsState()

    var state by remember {
        mutableStateOf(
            BuyerSearchState(
                hasLocationPermission =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
            )
        )
    }

    val ubicacionClienteInicial = remember { LatLng(-8.1116, -79.0287) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ubicacionClienteInicial, 17f)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        state = state.copy(
            hasLocationPermission =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        )
    }

    val gpsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            state = state.copy(locationTrigger = state.locationTrigger + 1)
        } else {
            state = state.copy(isLoadingLocation = false)
        }
    }

    LaunchedEffect(state.hasLocationPermission, state.locationTrigger) {
        if (!state.hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            state = state.copy(isLoadingLocation = true)
            val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 5000
            ).build()
            val builder = com.google.android.gms.location.LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
            val client = com.google.android.gms.location.LocationServices.getSettingsClient(context)
            val task = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                try {
                    searchViewModel.rastrearUbicacionActual(context)
                    val fusedLocationClient =
                        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(
                            context
                        )

                    @android.annotation.SuppressLint("MissingPermission")
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                LatLng(
                                    location.latitude,
                                    location.longitude
                                ), 16.5f
                            )
                            state = state.copy(isLoadingLocation = false)
                        } else {
                            @android.annotation.SuppressLint("MissingPermission")
                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                object : com.google.android.gms.location.LocationCallback() {
                                    override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                                        val lastLoc = result.lastLocation
                                        if (lastLoc != null) {
                                            cameraPositionState.position =
                                                CameraPosition.fromLatLngZoom(
                                                    LatLng(
                                                        lastLoc.latitude,
                                                        lastLoc.longitude
                                                    ), 16.5f
                                                )
                                            state = state.copy(isLoadingLocation = false)
                                            fusedLocationClient.removeLocationUpdates(this)
                                        }
                                    }
                                },
                                context.mainLooper
                            )
                        }
                    }.addOnFailureListener { state = state.copy(isLoadingLocation = false) }
                } catch (e: Exception) {
                    android.util.Log.e("BuyerSearchScreen", "Error al solicitar ubicacion", e)
                    state = state.copy(isLoadingLocation = false)
                }
            }

            task.addOnFailureListener { exception ->
                if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                    try {
                        gpsLauncher.launch(
                            androidx.activity.result.IntentSenderRequest.Builder(
                                exception.resolution.intentSender
                            ).build()
                        )
                    } catch (e: android.content.IntentSender.SendIntentException) {
                        android.util.Log.e("BuyerSearchScreen", "Error al lanzar el dialogo de activar GPS", e)
                        state = state.copy(isLoadingLocation = false)
                    }
                } else {
                    state = state.copy(isLoadingLocation = false)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFFFAFAFA)).padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = queryText,
                    onValueChange = { searchViewModel.onSearchQueryChange(it) },
                    placeholder = { Text("¿Qué deseas comer hoy?") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search, contentDescription = "Buscar", tint = Color(0xFFD32F2F),
                            modifier = Modifier.clickable { searchViewModel.ejecutarBusquedaInteligente() }
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Search),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSearch = { searchViewModel.ejecutarBusquedaInteligente() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD32F2F),
                        focusedLabelColor = Color(0xFFD32F2F),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(containerColor = Color(0xFFD32F2F)) { Text("$cartItemCount") }
                        }
                    }
                ) {
                    IconButton(
                        onClick = { cartViewModel.openReviewSheet() },
                        modifier = Modifier
                            .background(Color(0xFFECEFF1), shape = RoundedCornerShape(50.dp))
                            .size(48.dp)
                            .onGloballyPositioned { coords ->
                                val pos = coords.positionInWindow()
                                CartFlightBus.cartAnchor = Offset(
                                    x = pos.x + coords.size.width / 2f,
                                    y = pos.y + coords.size.height / 2f
                                )
                                android.util.Log.d("CartFlight", "cartAnchor = ${CartFlightBus.cartAnchor}")

                            }
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = Color(0xFF212121))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                if (ultimasBusquedas.isEmpty()) {
                    item {
                        Text("Aún no tienes búsquedas recientes", fontSize = 13.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(vertical = 8.dp))
                    }
                } else {
                    items(ultimasBusquedas, key = { it }) { historialItem ->
                        FilterChip(
                            selected = false,
                            onClick = {
                                searchViewModel.onSearchQueryChange(historialItem)
                                searchViewModel.ejecutarBusquedaInteligente()
                            },
                            label = { Text(historialItem, fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(containerColor = Color(0xFFEEEEEE), labelColor = Color(0xFF616161))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = state.hasLocationPermission),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            myLocationButtonEnabled = state.hasLocationPermission,
                            rotationGesturesEnabled = false,
                            tiltGesturesEnabled = false,
                            scrollGesturesEnabled = true,
                            zoomGesturesEnabled = true
                        )
                    ) {
                        huariquesRadar.forEach { huarique ->
                            MarkerInfoWindowContent(
                                state = rememberMarkerState(
                                    position = LatLng(
                                        huarique.latitud,
                                        huarique.longitud
                                    )
                                ),
                                title = huarique.nombre
                            ) {
                                Column(
                                    modifier = Modifier
                                        .background(Color.White, shape = RoundedCornerShape(12.dp))
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = huarique.nombre,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF212121)
                                    )
                                    Text(
                                        text = "${huarique.category} • ${huarique.distancia}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF757575)
                                    )
                                    Text(
                                        text = "Hace delivery hasta ${String.format(Locale.US, "%.1f", huarique.maxDeliveryDistanceKm)} km",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                    }

                    if (state.isLoadingLocation) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 12.dp
                                    ), verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Color(0xFFD32F2F)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        "Un momento, te estamos buscando...",
                                        fontSize = 13.sp,
                                        color = Color(0xFF212121)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            HuariquesRadarCarousel(
                huariquesRadar = huariquesRadar,
                cameraPositionState = cameraPositionState,
                searchViewModel = searchViewModel,
                onVerMenu = { huarique ->
                    state = state.copy(huariqueParaMenu = huarique, showMenuSheet = true)
                    searchViewModel.abrirMenuDeHuarique(huarique.id)
                }
            )
        }

        if (queryText.isNotEmpty() && platosEncontrados.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(top = 155.dp).background(Color.White)) {
                SearchResultsList(
                    platosEncontrados = platosEncontrados,
                    onAddDish = { plato ->
                        val huarique = huariquesRadar.find { it.id == plato.restaurantId }
                        cartViewModel.addDish(
                            restaurantId = plato.restaurantId,
                            restaurantName = huarique?.nombre ?: "Huarique",
                            dishId = plato.id,
                            dishName = plato.name,
                            unitPrice = plato.price.toPrecioDouble()
                        )
                    }
                )
            }
        }
    }

    if (state.showMenuSheet && state.huariqueParaMenu != null) {
        RestaurantMenuSheet(
            huarique = state.huariqueParaMenu!!,
            menu = menuDelHuarique,
            cartItemCount = cartItemCount,
            onDismiss = {
                state = state.copy(showMenuSheet = false, huariqueParaMenu = null)
                searchViewModel.cerrarMenuDeHuarique()
            },
            onDishAdded = { plato ->
                val huarique = state.huariqueParaMenu!!
                cartViewModel.addDish(
                    restaurantId = huarique.id,
                    restaurantName = huarique.nombre,
                    dishId = plato.id,
                    dishName = plato.name,
                    unitPrice = plato.price.toPrecioDouble()
                )
            }
        )
    }
}