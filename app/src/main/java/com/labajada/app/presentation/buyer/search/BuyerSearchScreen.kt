package com.labajada.app.presentation.buyer.search

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.labajada.app.core.extensions.toPrecioDouble
import com.labajada.app.presentation.buyer.cart.CartFlightBus
import com.labajada.app.presentation.buyer.cart.CartViewModel
import com.labajada.app.presentation.buyer.search.components.*
import com.labajada.app.presentation.shared.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerSearchScreen(
    searchViewModel: BuyerSearchViewModel,
    cartViewModel: CartViewModel
) {
    val context = LocalContext.current
    val density = LocalDensity.current
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

    var headerHeightDp by remember { mutableStateOf(120.dp) }

    val ubicacionClienteInicial = remember { LatLng(-8.1116, -79.0287) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ubicacionClienteInicial, 17f)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        state = state.copy(hasLocationPermission = granted)
    }

    val gpsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        state = if (result.resultCode == Activity.RESULT_OK) {
            state.copy(locationTrigger = state.locationTrigger + 1)
        } else {
            state.copy(isLoadingLocation = false)
        }
    }

    fun abrirIndicacionesGoogleMaps(latitud: Double, longitud: Double) {
        val uri = "https://www.google.com/maps/dir/?api=1&destination=$latitud,$longitud&travelmode=walking".toUri()
        val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    LaunchedEffect(state.hasLocationPermission) {
        if (!state.hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    DisposableEffect(state.hasLocationPermission, state.locationTrigger) {
        var activeCallback: com.google.android.gms.location.LocationCallback? = null
        var activeFusedClient: com.google.android.gms.location.FusedLocationProviderClient? = null

        if (state.hasLocationPermission) {
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
                        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
                    activeFusedClient = fusedLocationClient

                    @android.annotation.SuppressLint("MissingPermission")
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                LatLng(location.latitude, location.longitude), 16.5f
                            )
                            state = state.copy(isLoadingLocation = false)
                        } else {
                            val callback = object : com.google.android.gms.location.LocationCallback() {
                                override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                                    val lastLoc = result.lastLocation
                                    if (lastLoc != null) {
                                        cameraPositionState.position =
                                            CameraPosition.fromLatLngZoom(
                                                LatLng(lastLoc.latitude, lastLoc.longitude), 16.5f
                                            )
                                        state = state.copy(isLoadingLocation = false)
                                        fusedLocationClient.removeLocationUpdates(this)
                                    }
                                }
                            }
                            activeCallback = callback

                            @android.annotation.SuppressLint("MissingPermission")
                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                callback,
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

        onDispose {
            activeCallback?.let { callback ->
                activeFusedClient?.removeLocationUpdates(callback)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(IvoryBackground)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.onGloballyPositioned { coords ->
                    headerHeightDp = with(density) { coords.size.height.toDp() }
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = queryText,
                        onValueChange = { searchViewModel.onSearchQueryChange(it) },
                        placeholder = {
                            Text(
                                "¿Qué deseas comer hoy?",
                                fontFamily = Nunito,
                                color = TextoSecundario
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = RojoGochujang,
                                modifier = Modifier.clickable { searchViewModel.ejecutarBusquedaInteligente() }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Search
                        ),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                            onSearch = { searchViewModel.ejecutarBusquedaInteligente() }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RojoGochujang,
                            unfocusedBorderColor = BordeSuave,
                            focusedContainerColor = SuperficieCampo,
                            unfocusedContainerColor = SuperficieCampo,
                            focusedTextColor = NegroContorno,
                            unfocusedTextColor = NegroContorno
                        )
                    )

                    BadgedBox(
                        badge = {
                            if (cartItemCount > 0) {
                                Badge(
                                    containerColor = RojoGochujang,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        "$cartItemCount",
                                        fontFamily = Nunito,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = { cartViewModel.openReviewSheet() },
                            modifier = Modifier
                                .background(SuperficieCampo, shape = CircleShape)
                                .size(48.dp)
                                .onGloballyPositioned { coords ->
                                    val pos = coords.positionInWindow()
                                    CartFlightBus.cartAnchor = Offset(
                                        x = pos.x + coords.size.width / 2f,
                                        y = pos.y + coords.size.height / 2f
                                    )
                                }
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Carrito",
                                tint = NegroContorno
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (ultimasBusquedas.isEmpty()) {
                            item {
                                Text(
                                    "Aún no tienes búsquedas recientes",
                                    fontSize = 13.sp,
                                    fontFamily = Nunito,
                                    color = TextoSecundario,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        } else {
                            items(ultimasBusquedas, key = { it }) { historialItem ->
                                FilterChip(
                                    selected = false,
                                    onClick = {
                                        searchViewModel.onSearchQueryChange(historialItem)
                                        searchViewModel.ejecutarBusquedaInteligente()
                                    },
                                    label = {
                                        Text(
                                            historialItem,
                                            fontFamily = Nunito,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Eliminar búsqueda",
                                            tint = TextoSecundario,
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clickable {
                                                    searchViewModel.borrarTodoElHistorial()
                                                }
                                        )
                                    },
                                    shape = CircleShape,
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = false,
                                        borderColor = BordeSuave
                                    ),
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = SuperficieCampo,
                                        labelColor = TextoSecundario
                                    )
                                )
                            }
                        }
                    }

                    if (ultimasBusquedas.isNotEmpty()) {
                        Text(
                            "Limpiar",
                            fontSize = 12.sp,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.Bold,
                            color = RojoGochujang,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable {
                                    searchViewModel.borrarTodoElHistorial()
                                }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BordeSuave),
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
                            val markerState = rememberMarkerState(
                                position = LatLng(
                                    huarique.latitud,
                                    huarique.longitud
                                )
                            )

                            MarkerInfoWindowContent(
                                state = markerState,
                                title = huarique.nombre,
                                onInfoWindowClick = {
                                    abrirIndicacionesGoogleMaps(
                                        latitud = huarique.latitud,
                                        longitud = huarique.longitud
                                    )
                                }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .background(IvoryBackground, shape = RoundedCornerShape(12.dp))
                                        .border(1.dp, NaranjaCercania, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = huarique.nombre,
                                        fontSize = 14.sp,
                                        fontFamily = Baloo2,
                                        fontWeight = FontWeight.Bold,
                                        color = NegroContorno
                                    )
                                    Text(
                                        text = "${huarique.category} • ${huarique.distancia}",
                                        fontSize = 12.sp,
                                        fontFamily = Nunito,
                                        color = TextoSecundario
                                    )
                                    Text(
                                        text = "Hace delivery hasta ${String.format(Locale.US, "%.1f", huarique.maxDeliveryDistanceKm)} km",
                                        fontSize = 12.sp,
                                        fontFamily = Nunito,
                                        fontWeight = FontWeight.Bold,
                                        color = VerdeMatcha
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Directions,
                                            contentDescription = "Cómo llegar",
                                            tint = NaranjaCercania,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Toca esta tarjeta para ver cómo llegar ➔",
                                            fontSize = 11.sp,
                                            fontFamily = Nunito,
                                            fontWeight = FontWeight.Bold,
                                            color = NaranjaCercania
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
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

                    // Dentro de tu Box:
                    Column(
                        modifier = Modifier.align(Alignment.TopCenter)
                    ) {
                        AnimatedVisibility(
                            visible = state.isLoadingLocation,
                            modifier = Modifier.padding(top = 12.dp),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = IvoryBackground,
                                shadowElevation = 4.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = RojoGochujang
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Buscando tu ubicación...",
                                        fontSize = 12.sp,
                                        fontFamily = Nunito,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NegroContorno
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        val overlayTopOffset = 16.dp + headerHeightDp + 10.dp

        if (queryText.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = overlayTopOffset)
                    .background(IvoryBackground)
            ) {
                if (platosEncontrados.isNotEmpty()) {
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
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = BordeSuave,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No encontramos platos para \"$queryText\"",
                            fontSize = 15.sp,
                            fontFamily = Baloo2,
                            fontWeight = FontWeight.Bold,
                            color = NegroContorno,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Prueba con otro nombre o revisa los huariques cercanos en el mapa",
                            fontSize = 13.sp,
                            fontFamily = Nunito,
                            color = TextoSecundario,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
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