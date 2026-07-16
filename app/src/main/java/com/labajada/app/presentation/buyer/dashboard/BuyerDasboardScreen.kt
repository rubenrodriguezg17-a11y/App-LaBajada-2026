package com.labajada.app.presentation.buyer.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.labajada.app.domain.model.Cart
import com.labajada.app.domain.model.Direccion
import com.labajada.app.domain.model.Order
import com.labajada.app.presentation.buyer.cart.CartReviewSheet
import com.labajada.app.presentation.buyer.cart.CartViewModel
import com.labajada.app.presentation.buyer.cart.FlyToCartOverlay
import com.labajada.app.presentation.buyer.search.BuyerSearchScreen
import com.labajada.app.presentation.buyer.search.BuyerSearchViewModel
import com.labajada.app.presentation.buyer.search.components.BuyerPerfilTab
import com.labajada.app.presentation.buyer.tracking.OrderTrackingScreen
import com.labajada.app.presentation.buyer.tracking.OrderTrackingViewModel
import com.labajada.app.presentation.shared.effects.ConfettiOverlay
import com.labajada.app.presentation.shared.effects.OrderSuccessDialog

private data class BuyerNavItem(val label: String, val icon: ImageVector, val index: Int)

@Composable
fun BuyerDashboardScreen(
    searchViewModel: BuyerSearchViewModel,
    cartViewModel: CartViewModel,
    trackingViewModel: OrderTrackingViewModel,
    onSwitchToRestaurantMode: () -> Unit,
    onAccountDeactivated: () -> Unit,
    onLogout: () -> Unit
) {
    var lastOrderSnapshot by remember { mutableStateOf<Cart?>(null) }

    var lastOrder by remember { mutableStateOf<Order?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var confettiTrigger by remember { mutableLongStateOf(0L) }

    var selectedTab by remember { mutableIntStateOf(0) }
    val activeOrdersCount by trackingViewModel.activeOrdersCount.collectAsState()

    val cart by cartViewModel.cart.collectAsState()
    val cartItemCount by cartViewModel.itemCount.collectAsState()
    val showReviewSheet by cartViewModel.showReviewSheet.collectAsState()
    val isCheckingOut by cartViewModel.isCheckingOut.collectAsState()
    val checkoutError by cartViewModel.checkoutError.collectAsState()
    val currentBuyerName by searchViewModel.currentBuyerName.collectAsState()
    val ubicacionActual by searchViewModel.userLocation.collectAsState()


    val navItems = listOf(
        BuyerNavItem("Inicio", Icons.Default.Home, 0),
        BuyerNavItem("Pedidos", Icons.Default.ShoppingBag, 1),
        BuyerNavItem("Perfil", Icons.Default.Person, 2)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                navItems.forEach { item ->
                    val isSelected = selectedTab == item.index
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.1f else 1f,
                        animationSpec = tween(200),
                        label = "nav_icon_scale"
                    )
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = item.index },
                        icon = {
                            BadgedBox(badge = {
                                if (item.index == 1 && activeOrdersCount > 0) {
                                    Badge(containerColor = Color(0xFFD32F2F)) { Text("$activeOrdersCount") }
                                }
                            }) {
                                Icon(item.icon, contentDescription = item.label, modifier = Modifier.scale(scale))
                            }
                        },
                        label = { Text(item.label, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFD32F2F),
                            selectedTextColor = Color(0xFFD32F2F),
                            indicatorColor = Color(0xFFFFEBEE),
                            unselectedIconColor = Color(0xFF9E9E9E),
                            unselectedTextColor = Color(0xFF9E9E9E)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (selectedTab) {
                0 -> BuyerSearchScreen(searchViewModel = searchViewModel, cartViewModel = cartViewModel)
                1 -> OrderTrackingScreen(viewModel = trackingViewModel)
                2 -> BuyerPerfilTab(
                    searchViewModel = searchViewModel,
                    onSwitchToRestaurantMode = onSwitchToRestaurantMode,
                    onAccountDeactivated = onAccountDeactivated,
                    onLogout = onLogout
                )
            }

            FlyToCartOverlay()
            ConfettiOverlay(trigger = confettiTrigger)
        }
    }

    if (showReviewSheet && cart != null) {
        CartReviewSheet(
            cart = cart!!,
            isCheckingOut = isCheckingOut,
            checkoutError = checkoutError,
            onQuantityChange = { dishId, newQty -> cartViewModel.updateQuantity(dishId, newQty) },
            onDeliverySelectedChange = { cartViewModel.setDeliverySelected(it) },
            onConfirm = {
                lastOrderSnapshot = cart
                cartViewModel.confirmarPedido(currentBuyerName.ifBlank { "Comensal" },
                    direccion = Direccion(
                        latitud = ubicacionActual.latitude,
                        longitud = ubicacionActual.longitude
                    )
                    ) { order ->
                    lastOrder = order
                    showSuccessDialog = true
                    confettiTrigger = System.currentTimeMillis()
                }
            },
            onDismiss = { cartViewModel.closeReviewSheet() }
        )
    }

    if (showSuccessDialog) {
        OrderSuccessDialog(
            cart = lastOrderSnapshot,
            order = lastOrder,
            buyerName = currentBuyerName.ifBlank { "Comensal" },
            onDismiss = {
                showSuccessDialog = false
                selectedTab = 1
            }
        )
    }
}