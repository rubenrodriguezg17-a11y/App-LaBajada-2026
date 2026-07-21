package com.labajada.app.presentation.buyer.search

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.labajada.app.core.extensions.toPrecioDouble
import com.labajada.app.presentation.buyer.cart.CartFlightBus
import com.labajada.app.presentation.buyer.cart.CartViewModel
import com.labajada.app.presentation.buyer.search.components.*
import com.labajada.app.presentation.shared.theme.*
import kotlinx.coroutines.tasks.await

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
                hasLocationPermission = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
        )
    }

    var headerHeightDp by remember { mutableStateOf(120.dp) }

    val defaultLocation = remember { LatLng(-8.1116, -79.0287) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 16f)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        state = state.copy(
            hasLocationPermission = granted,
            // Al conceder el permiso, disparamos automáticamente la primera búsqueda de ubicación.
            locationTrigger = if (granted) state.locationTrigger + 1 else state.locationTrigger
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

    fun abrirIndicacionesGoogleMaps(latitud: Double, longitud: Double) {
        // google.navigation:q=lat,lng&mode=w -> abre Google Maps directo en modo
        // navegación turn-by-turn a pie (como si el usuario ya hubiera tocado "Iniciar"),
        // sin pasar por la pantalla de previsualización de ruta.
        val navigationUri = "google.navigation:q=$latitud,$longitud&mode=w".toUri()
        val navigationIntent = Intent(Intent.ACTION_VIEW, navigationUri).apply {
            setPackage("com.google.android.apps.maps")
        }

        if (navigationIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(navigationIntent)
        } else {
            // Fallback: si no tiene la app de Maps instalada, cae al navegador
            // (esto sí abre en modo previsualización, no hay forma de auto-iniciar por web).
            val webUri = "https://www.google.com/maps/dir/?api=1&destination=$latitud,$longitud&travelmode=walking".toUri()
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }

    LaunchedEffect(Unit) {
        if (!state.hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            // Ya tenía el permiso concedido de una sesión anterior: disparamos
            // la búsqueda de ubicación de una vez, sin esperar a que toque el FAB.
            state = state.copy(locationTrigger = state.locationTrigger + 1)
        }
    }

    // Fix bug crítico: este efecto es el que realmente conecta el FAB de "Mi ubicación"
    // (y el permiso recién concedido) con el GPS. Antes locationTrigger se incrementaba
    // pero nada lo escuchaba.
    LaunchedEffect(state.locationTrigger, state.hasLocationPermission) {
        if (!state.hasLocationPermission || state.locationTrigger == 0) return@LaunchedEffect

        state = state.copy(isLoadingLocation = true)

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10_000L
        ).build()
        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()
        val settingsClient = LocationServices.getSettingsClient(context)

        val gpsListo = try {
            settingsClient.checkLocationSettings(settingsRequest).await()
            true
        } catch (e: ResolvableApiException) {
            try {
                val intentSenderRequest = IntentSenderRequest.Builder(e.resolution).build()
                gpsLauncher.launch(intentSenderRequest)
            } catch (_: Exception) {
                // No se pudo lanzar el diálogo de activación de GPS; continuamos sin bloquear.
            }
            false
        } catch (e: Exception) {
            false
        }

        if (gpsListo) {
            searchViewModel.rastrearUbicacionActual(context) {
                state = state.copy(isLoadingLocation = false)
            }
        } else {
            // Si el GPS estaba apagado, gpsLauncher ya se encargó de pedir que se active;
            // el resultado de ese diálogo vuelve a incrementar locationTrigger y reintenta.
            state = state.copy(isLoadingLocation = false)
        }
    }

    // Mueve la cámara cada vez que la ubicación real del usuario cambia.
    val userLocation by searchViewModel.userLocation.collectAsState()
    LaunchedEffect(userLocation) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(userLocation, 16f)
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(IvoryBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.onGloballyPositioned { coords ->
                    headerHeightDp = with(density) { coords.size.height.toDp() }
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = queryText,
                        onValueChange = { searchViewModel.onSearchQueryChange(it) },
                        placeholder = {
                            Text(
                                "¿Qué quieres comer hoy?",
                                fontFamily = Nunito,
                                color = TextoSecundario,
                                fontSize = 14.sp
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
                        trailingIcon = {
                            if (queryText.isNotEmpty()) {
                                IconButton(onClick = { searchViewModel.onSearchQueryChange("") }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Limpiar texto",
                                        tint = TextoSecundario
                                    )
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
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
                        Surface(
                            onClick = { cartViewModel.openReviewSheet() },
                            shape = CircleShape,
                            color = SuperficieCampo,
                            border = BorderStroke(1.dp, BordeSuave),
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .size(50.dp)
                                .onGloballyPositioned { coords ->
                                    val pos = coords.positionInWindow()
                                    CartFlightBus.cartAnchor = Offset(
                                        x = pos.x + coords.size.width / 2f,
                                        y = pos.y + coords.size.height / 2f
                                    )
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Carrito",
                                    tint = NegroContorno
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

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
                                    "Descubre huariques y platos cerca de ti",
                                    fontSize = 12.sp,
                                    fontFamily = Nunito,
                                    color = TextoSecundario,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        } else {
                            items(ultimasBusquedas, key = { it }) { historialItem ->
                                InputChip(
                                    selected = false,
                                    onClick = {
                                        searchViewModel.onSearchQueryChange(historialItem)
                                        searchViewModel.ejecutarBusquedaInteligente()
                                    },
                                    label = {
                                        Text(
                                            historialItem,
                                            fontFamily = Nunito,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
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
                                                    searchViewModel.eliminarBusquedaIndividual(historialItem)
                                                }
                                        )
                                    },
                                    shape = CircleShape,
                                    border = InputChipDefaults.inputChipBorder(
                                        enabled = true,
                                        selected = false,
                                        borderColor = BordeSuave
                                    ),
                                    colors = InputChipDefaults.inputChipColors(
                                        containerColor = SuperficieCampo,
                                        labelColor = NegroContorno
                                    )
                                )
                            }
                        }
                    }

                    if (ultimasBusquedas.isNotEmpty()) {
                        TextButton(
                            onClick = { searchViewModel.borrarTodoElHistorial() },
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                "Limpiar",
                                fontSize = 12.sp,
                                fontFamily = Nunito,
                                fontWeight = FontWeight.Bold,
                                color = RojoGochujang
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(24.dp),
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
                            myLocationButtonEnabled = false,
                            rotationGesturesEnabled = false,
                            tiltGesturesEnabled = false,
                            scrollGesturesEnabled = !state.isLoadingLocation,
                            zoomGesturesEnabled = !state.isLoadingLocation
                        )
                    ) {
                        huariquesRadar.forEach { huarique ->
                            val markerState = rememberMarkerState(
                                position = LatLng(huarique.latitud, huarique.longitud)
                            )

                            MarkerInfoWindowContent(
                                state = markerState,
                                title = huarique.nombre,
                                onInfoWindowClick = {
                                    abrirIndicacionesGoogleMaps(huarique.latitud, huarique.longitud)
                                }
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = IvoryBackground,
                                    border = BorderStroke(1.dp, NaranjaCercania),
                                    shadowElevation = 4.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
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
                                            text = "Delivery hasta ${huarique.maxDeliveryDistanceKm} km",
                                            fontSize = 11.sp,
                                            fontFamily = Nunito,
                                            fontWeight = FontWeight.Bold,
                                            color = VerdeMatcha
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.padding(top = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Directions,
                                                contentDescription = null,
                                                tint = NaranjaCercania,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = "Cómo llegar ➔",
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
                    }

                    SmallFloatingActionButton(
                        onClick = {
                            state = state.copy(locationTrigger = state.locationTrigger + 1)
                        },
                        containerColor = IvoryBackground,
                        contentColor = NegroContorno,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Mi ubicación",
                            tint = RojoGochujang
                        )
                    }

                    androidx.compose.animation.AnimatedVisibility(
                        visible = state.isLoadingLocation,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = IvoryBackground,
                            border = BorderStroke(1.dp, BordeSuave),
                            shadowElevation = 8.dp,
                            modifier = Modifier
                                .padding(horizontal = 32.dp)
                                .fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = SuperficieCampo,
                                    modifier = Modifier.size(56.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.NearMe,
                                            contentDescription = null,
                                            tint = RojoGochujang,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Buscando huariques cerca de ti...",
                                    fontSize = 16.sp,
                                    fontFamily = Baloo2,
                                    fontWeight = FontWeight.Bold,
                                    color = NegroContorno,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Estamos obteniendo tu ubicación para mostrarte las mejores opciones a tu alrededor.",
                                    fontSize = 13.sp,
                                    fontFamily = Nunito,
                                    color = TextoSecundario,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                LinearProgressIndicator(
                                    color = RojoGochujang,
                                    trackColor = SuperficieCampo,
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .height(4.dp)
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
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
                }
            }
        }

        val overlayTopOffset = 12.dp + headerHeightDp

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
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Sin resultados para \"$queryText\"",
                            fontSize = 16.sp,
                            fontFamily = Baloo2,
                            fontWeight = FontWeight.Bold,
                            color = NegroContorno,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Intenta buscar términos más generales como \"Ceviche\", \"Hamburguesa\" o explora el mapa.",
                            fontSize = 13.sp,
                            fontFamily = Nunito,
                            color = TextoSecundario,
                            textAlign = TextAlign.Center
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