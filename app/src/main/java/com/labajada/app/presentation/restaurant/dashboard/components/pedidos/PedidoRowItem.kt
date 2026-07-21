package com.labajada.app.presentation.restaurant.dashboard.components.pedidos

import android.content.Intent
import androidx.core.net.toUri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.buyerName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = if (order.isDeliverySelected) "🛵 Delivery a domicilio" else "🏪 Recojo en local",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF616161)
                    )
                }
                Surface(
                    color = colorTag,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = tagEstado,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorTagText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Surface(
                color = Color(0xFFF8F9FA),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.quantity}x ${item.dishName}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF37474F)
                            )
                        }
                    }
                }
            }

            if (order.isDeliverySelected && order.direccion != null) {
                val context = LocalContext.current

                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val lat = order.direccion.latitud
                            val lng = order.direccion.longitud
                            val uri = "https://www.google.com/maps/dir/?api=1&destination=$lat,$lng&travelmode=two_wheeler".toUri()
                            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TwoWheeler,
                            contentDescription = "Ruta en moto",
                            tint = Color(0xFF1565C0),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Ver ruta de entrega en Google Maps",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF0F0F0))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF757575)
                    )
                    Text(
                        text = "S/. ${String.format(Locale.US, "%.2f", order.totalPrice)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1B5E20)
                    )
                }

                if (order.status != OrderStatus.ENTREGADO) {
                    Button(
                        onClick = onAccionClick,
                        colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = textoBoton,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
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
        // Nuevo pedido: Rojo carmesí intenso que exige atención e incita la urgencia de aceptar.
        OrderStatus.ENVIADO -> EstadoUi(
            tag = "¡NUEVO PEDIDO!",
            colorTag = Color(0xFFFFEBEE),
            colorTagText = Color(0xFFC62828),
            colorBoton = Color(0xFFD32F2F),
            textoBoton = "Aceptar pedido"
        )
        // En preparación: Naranja cálido que comunica trabajo activo en cocina.
        OrderStatus.PREPARACION -> EstadoUi(
            tag = "En preparación",
            colorTag = Color(0xFFFFF3E0),
            colorTagText = Color(0xFFE65100),
            colorBoton = Color(0xFFF57C00),
            textoBoton = if (isDelivery) "Enviar delivery" else "Listo para recojo"
        )
        // En camino: Azul/Violeta de despacho y movimiento.
        OrderStatus.EN_CAMINO -> EstadoUi(
            tag = "En camino",
            colorTag = Color(0xFFE8EAF6),
            colorTagText = Color(0xFF283593),
            colorBoton = Color(0xFF2E7D32),
            textoBoton = "Marcar entregado"
        )
        // Listo para recojo: Verde menta suave que indica logro y espera de cliente.
        OrderStatus.LISTO_RECOJO -> EstadoUi(
            tag = "Listo en local",
            colorTag = Color(0xFFE8F5E9),
            colorTagText = Color(0xFF2E7D32),
            colorBoton = Color(0xFF2E7D32),
            textoBoton = "Marcar entregado"
        )
        // Entregado: Estado final deshabilitado.
        OrderStatus.ENTREGADO -> EstadoUi(
            tag = "Entregado",
            colorTag = Color(0xFFF5F5F5),
            colorTagText = Color(0xFF616161),
            colorBoton = Color.Gray,
            textoBoton = ""
        )
    }
}