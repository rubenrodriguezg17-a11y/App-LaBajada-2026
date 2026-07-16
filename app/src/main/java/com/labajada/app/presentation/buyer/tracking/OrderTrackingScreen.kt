package com.labajada.app.presentation.buyer.tracking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Moped
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.labajada.app.domain.model.Order
import com.labajada.app.domain.model.OrderStatus
import com.labajada.app.domain.model.Restaurant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrderTrackingScreen(viewModel: OrderTrackingViewModel) {
    val orders by viewModel.orders.collectAsState()
    val restaurantsById by viewModel.restaurantsById.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }

    val activos = orders.filter { it.status != OrderStatus.ENTREGADO }
    val historial = orders.filter { it.status == OrderStatus.ENTREGADO }
    val filteredOrders = if (selectedTabIndex == 0) activos else historial

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Mis Pedidos",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF212121),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 16.dp)
        )

        // --- Selector tipo pastilla ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEEEEEE), RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SegmentedTabButton(
                modifier = Modifier.weight(1f),
                selected = selectedTabIndex == 0,
                label = "Pedidos Activos",
                badgeCount = activos.size,
                onClick = { selectedTabIndex = 0 }
            )
            SegmentedTabButton(
                modifier = Modifier.weight(1f),
                selected = selectedTabIndex == 1,
                label = "Historial de Pedidos",
                badgeCount = null,
                onClick = { selectedTabIndex = 1 }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (selectedTabIndex == 0) Icons.Default.Moped else Icons.Default.Storefront,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF263238)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (selectedTabIndex == 0) "No tienes pedidos activos" else "Aún no tienes pedidos entregados",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238)
                    )

                    Text(
                        text = if (selectedTabIndex == 0) "Cuando pidas algo, lo verás aquí" else "Tu historial aparecerá cuando completes un pedido",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                items(filteredOrders, key = { it.id }) { order ->
                    OrderTrackingCard(order, restaurantsById[order.restaurantId])
                }
            }
        }
    }
}

@Composable
private fun SegmentedTabButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    label: String,
    badgeCount: Int?,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color(0xFF212121) else Color(0xFF9E9E9E)
        )
        if (badgeCount != null && badgeCount > 0) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD32F2F)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "$badgeCount", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun OrderTrackingCard(order: Order, restaurant: Restaurant?) {
    var showDetails by remember { mutableStateOf(false) }

    val steps = if (order.isDeliverySelected) {
        listOf(
            OrderStatus.ENVIADO to "Enviado",
            OrderStatus.PREPARACION to "En preparación",
            OrderStatus.EN_CAMINO to "En camino",
            OrderStatus.ENTREGADO to "Entregado"
        )
    } else {
        listOf(
            OrderStatus.ENVIADO to "Enviado",
            OrderStatus.PREPARACION to "En preparación",
            OrderStatus.LISTO_RECOJO to "Listo",
            OrderStatus.ENTREGADO to "Entregado"
        )
    }
    val currentIndex = steps.indexOfFirst { it.first == order.status }.coerceAtLeast(0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

            // --- Encabezado: imagen + nombre + orden + estimado ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5))
                ) {
                    if (restaurant?.imageUrl != null) {
                        AsyncImage(
                            model = restaurant.imageUrl,
                            contentDescription = restaurant.restaurantName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.TableRestaurant, contentDescription = null, tint = Color(0xFFBDBDBD))
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = restaurant?.restaurantName ?: "Restaurante",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF212121)
                    )
                    Text(
                        text = "Orden #${order.id.takeLast(5).uppercase()}",
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E)
                    )
                    Text(
                        text = estimadoParaEstado(order.status, order.isDeliverySelected),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            // --- Stepper con íconos y etiquetas ---
            if (order.status != OrderStatus.ENTREGADO || true) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        steps.forEachIndexed { index, (status, _) ->
                            val isDone = index < currentIndex
                            val isCurrent = index == currentIndex

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isDone || isCurrent -> Color(0xFF2E7D32)
                                            else -> Color(0xFFE0E0E0)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    isCurrent && status == OrderStatus.EN_CAMINO -> Icon(
                                        Icons.Default.DeliveryDining, contentDescription = null,
                                        tint = Color.White, modifier = Modifier.size(16.dp)
                                    )
                                    isCurrent && status == OrderStatus.LISTO_RECOJO -> Icon(
                                        Icons.Default.Storefront, contentDescription = null,
                                        tint = Color.White, modifier = Modifier.size(16.dp)
                                    )
                                    isDone || isCurrent -> Icon(
                                        Icons.Default.Check, contentDescription = null,
                                        tint = Color.White, modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            if (index < steps.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(3.dp)
                                        .background(if (index < currentIndex) Color(0xFF2E7D32) else Color(0xFFE0E0E0))
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        steps.forEachIndexed { index, (_, label) ->
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (index == currentIndex) FontWeight.Bold else FontWeight.Medium,
                                color = if (index <= currentIndex) Color(0xFF212121) else Color(0xFFBDBDBD),
                                modifier = Modifier.weight(1f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = showDetails) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    order.items.forEach { item ->
                        Text(
                            text = "${item.quantity}x ${item.dishName}",
                            fontSize = 13.sp,
                            color = Color(0xFF616161)
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = if (order.isDeliverySelected) "Delivery" else "Recojo en local",
                            fontSize = 12.sp, color = Color(0xFF757575)
                        )
                        Text(
                            text = "S/. ${String.format(Locale.US, "%.2f", order.totalPrice)}",
                            fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFFD32F2F)
                        )
                    }
                    Text(
                        text = SimpleDateFormat("dd MMM, HH:mm", Locale("es")).format(Date(order.timestamp)),
                        fontSize = 11.sp, color = Color(0xFF9E9E9E)
                    )
                }
            }

            OutlinedButton(
                onClick = { showDetails = !showDetails },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (showDetails) "Ocultar Detalles" else "Ver Detalles",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }
        }
    }
}

private fun estimadoParaEstado(status: OrderStatus, isDelivery: Boolean): String = when (status) {
    OrderStatus.ENVIADO -> "Esperando confirmación del restaurante"
    OrderStatus.PREPARACION -> if (isDelivery) "Llega en 25-35 min" else "Listo en 15-20 min"
    OrderStatus.EN_CAMINO -> "Llega en 10-15 min"
    OrderStatus.LISTO_RECOJO -> "Listo para recoger"
    OrderStatus.ENTREGADO -> "Pedido entregado"
}