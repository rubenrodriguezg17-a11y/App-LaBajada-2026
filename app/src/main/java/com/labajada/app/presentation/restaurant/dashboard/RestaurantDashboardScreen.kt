package com.labajada.app.presentation.restaurant.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.presentation.restaurant.dashboard.components.CollapsibleStatusSection
import com.labajada.app.presentation.restaurant.dashboard.components.DashboardHeader
import com.labajada.app.presentation.restaurant.dashboard.components.DashboardModals
import com.labajada.app.presentation.restaurant.dashboard.components.historial.HistorialScreen
import com.labajada.app.presentation.restaurant.dashboard.components.menu.MenuScreen
import com.labajada.app.presentation.restaurant.dashboard.components.pedidos.PedidosScreen
import com.labajada.app.presentation.restaurant.dashboard.components.perfil.PerfilScreen

data class BottomNavItem(val label: String, val icon: ImageVector, val index: Int)

private val CharcoalPremiumColor = Color(0xFF161618)
private val WarmOrangeAccent = Color(0xFFFF9800)
private val OffWhiteSurface = Color(0xFFF6F6F6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDashboardScreen(
    viewModel: RestaurantDashboardViewModel,
    onSwitchToBuyerMode: () -> Unit,
    onAccountDeactivated: () -> Unit,
    onNoRestaurantFound: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val platillosDelDia by viewModel.platillosDelDia.collectAsState()
    val pedidosActivosList by viewModel.pedidosActivos.collectAsState()
    val gananciasHoyCalculadas by viewModel.gananciasHoy.collectAsState()
    val session by viewModel.activeSession.collectAsState()
    val backupName by viewModel.fallbackRestaurantName.collectAsState()

    val hasRestaurant by viewModel.hasRestaurant.collectAsState()

    LaunchedEffect(hasRestaurant) {
        if (hasRestaurant == false) {
            onNoRestaurantFound()
        }
    }
    val nameRestaurant = session?.restaurantName ?: uiState.resNameByOwner.ifBlank { backupName }

    val bottomNavItems = listOf(
        BottomNavItem("Pedidos", Icons.Default.ShoppingCart, 0),
        BottomNavItem("Mi Menú", Icons.Default.Menu, 1),
        BottomNavItem("Historial", Icons.Default.History, 2),  // ← nuevo
        BottomNavItem("Perfil", Icons.Default.Person, 3)
    )

    if (hasRestaurant != true) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF263238))
        }
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp,
                modifier = Modifier.shadow(elevation = 8.dp)
            ) {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = uiState.selectedTab == item.index,
                        onClick = { viewModel.onTabSelected(item.index) },
                        icon = {
                            BadgedBox(badge = {
                                if (item.index == 0 && pedidosActivosList.isNotEmpty()) {
                                    Badge { Text("${pedidosActivosList.size}") }
                                }
                            }) { Icon(item.icon, contentDescription = item.label) }
                        },
                        label = { Text(item.label, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CharcoalPremiumColor,
                            selectedTextColor = CharcoalPremiumColor,
                            indicatorColor = Color(0xFFEEEEEE),
                            unselectedIconColor = Color(0xFF9E9E9E),
                            unselectedTextColor = Color(0xFF9E9E9E)
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (uiState.selectedTab == 1) {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.prepararNuevoPlatillo()
                        viewModel.toggleFormSheet(true)
                    },
                    containerColor = CharcoalPremiumColor,
                    contentColor = WarmOrangeAccent,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nuevo Platillo", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->

        when (uiState.selectedTab) {
            3 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(OffWhiteSurface)
                        .padding(paddingValues)
                ) {
                    PerfilScreen(
                        viewModel = viewModel,
                        onSwitchToBuyerMode = onSwitchToBuyerMode,
                        onAccountDeactivated = onAccountDeactivated,
                        onLogout = onLogout
                    )                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CharcoalPremiumColor)
                        .padding(paddingValues)
                ) {
                    Column(
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp)
                    ) {
                        Text(
                            text = "Ey! $nameRestaurant",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "¿Qué haremos hoy?",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.60f)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        DashboardHeader(
                            pedidosEnCola = pedidosActivosList.size,
                            gananciasHoy = gananciasHoyCalculadas,
                            isGananciasVisible = uiState.isGananciasVisible,
                            onToggleGanancias = { viewModel.toggleGananciasVisibility() }
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(OffWhiteSurface, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                    ) {
                        CollapsibleStatusSection(
                            isOpen = uiState.resIsOpen,
                            onToggleIsOpen = { viewModel.toggleIsOpen() }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        when (uiState.selectedTab) {
                            0 -> PedidosScreen(viewModel = viewModel, pedidosActivosList = pedidosActivosList)
                            1 -> MenuScreen(viewModel = viewModel, platillosDelDia = platillosDelDia)
                            2 -> {
                                val pedidosCompletados by viewModel.pedidosCompletados.collectAsState()
                                HistorialScreen(pedidosCompletados = pedidosCompletados)
                            }
                        }
                    }
                }
            }
        }
    }

    DashboardModals(viewModel = viewModel)
}