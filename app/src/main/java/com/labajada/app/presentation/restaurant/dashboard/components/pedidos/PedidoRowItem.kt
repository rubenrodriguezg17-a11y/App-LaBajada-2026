package com.labajada.app.presentation.restaurant.dashboard.components.pedidos

import android.content.Intent
import androidx.core.net.toUri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.domain.model.Order
import com.labajada.app.domain.model.OrderStatus
import java.util.Locale

@Composable
fun PedidoRowItem(
    order: Order,
    onAccionClick: () -> Unit
) {
    val (tagEstado, colorTag, colorTagText, colorBoton, textoBoton) = estadoUi(order.status, order.isDeliverySelected)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(order.buyerName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
                    Text(
                        text = if (order.isDeliverySelected) " Delivery" else " Recojo en local",
                        fontSize = 11.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }
                Card(colors = CardDefaults.cardColors(containerColor = colorTag), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = tagEstado,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorTagText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                order.items.forEach { item ->
                    Text(
                        text = "${item.quantity}x ${item.dishName}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            if (order.isDeliverySelected && order.direccion != null){
                val context = androidx.compose.ui.platform.LocalContext.current

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable{
                            val uri = "geo:${order.direccion.latitud},${order.direccion.longitud}?q=${order.direccion.latitud},${order.direccion.longitud}".toUri()
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.setPackage("com.google.android.apps.maps")
                            try {
                                context.startActivity(intent)
                            }catch (e: Exception) {
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ubicacion",
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Ver ubicación de entrega",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFEEEEEE))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "S/. ${String.format(Locale.US, "%.2f", order.totalPrice)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2E7D32)
                )
                if (order.status != OrderStatus.ENTREGADO) {
                    Button(
                        onClick = onAccionClick,
                        colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(textoBoton, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

private data class EstadoUi(
    val tag: String,
    val colorTag: Color,
    val colorTagText: Color,
    val colorBoton: Color,
    val textoBoton: String
)

private fun estadoUi(status: OrderStatus, isDelivery: Boolean): EstadoUi {
    return when (status) {
        OrderStatus.ENVIADO -> EstadoUi(
            tag = "Nuevo pedido",
            colorTag = Color(0xFFFFF3E0),
            colorTagText = Color(0xFFE65100),
            colorBoton = Color(0xFF1976D2),
            textoBoton = "Aceptar"
        )
        OrderStatus.PREPARACION -> EstadoUi(
            tag = "En preparación",
            colorTag = Color(0xFFE3F2FD),
            colorTagText = Color(0xFF1565C0),
            colorBoton = Color(0xFF7B1FA2),
            textoBoton = if (isDelivery) "Enviar" else "Marcar listo"
        )
        OrderStatus.EN_CAMINO -> EstadoUi(
            tag = "En camino",
            colorTag = Color(0xFFF3E5F5),
            colorTagText = Color(0xFF7B1FA2),
            colorBoton = Color(0xFF2E7D32),
            textoBoton = "Marcar entregado"
        )
        OrderStatus.LISTO_RECOJO -> EstadoUi(
            tag = "Listo para recojo",
            colorTag = Color(0xFFF3E5F5),
            colorTagText = Color(0xFF7B1FA2),
            colorBoton = Color(0xFF2E7D32),
            textoBoton = "Marcar entregado"
        )
        OrderStatus.ENTREGADO -> EstadoUi(
            tag = "Entregado",
            colorTag = Color(0xFFE8F5E9),
            colorTagText = Color(0xFF2E7D32),
            colorBoton = Color.Gray,
            textoBoton = ""
        )
    }
}