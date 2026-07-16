package com.labajada.app.presentation.restaurant.dashboard.components.historial

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.domain.model.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistorialScreen(pedidosCompletados: List<Order>) {
    if (pedidosCompletados.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = "Historial vacío",
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFF263238)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Aún no tienes pedidos entregados", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
                Text("Aquí verás tu historial de ventas", fontSize = 13.sp, color = Color.Gray)
            }
        }
    } else {
        val totalVendido = pedidosCompletados.sumOf { it.totalPrice }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF263238)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total histórico entregado", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                        Text(
                            "S/. ${String.format(Locale.US, "%.2f", totalVendido)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text("${pedidosCompletados.size} pedidos entregados", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }

            items(pedidosCompletados, key = { it.id }) { order ->
                HistorialOrderCard(order)
            }
        }
    }
}

@Composable
private fun HistorialOrderCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(order.buyerName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                Text(
                    SimpleDateFormat("dd MMM, HH:mm", Locale("es")).format(Date(order.timestamp)),
                    fontSize = 11.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
            Text(
                order.items.joinToString(", ") { "${it.quantity}x ${it.dishName}" },
                fontSize = 13.sp,
                color = Color(0xFF757575)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    if (order.isDeliverySelected) " Delivery" else "🏠 Recojo en local",
                    fontSize = 11.sp,
                    color = Color(0xFF9E9E9E)
                )
                Text(
                    "S/. ${String.format(Locale.US, "%.2f", order.totalPrice)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}