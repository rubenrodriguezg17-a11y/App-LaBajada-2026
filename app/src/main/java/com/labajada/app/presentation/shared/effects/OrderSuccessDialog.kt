package com.labajada.app.presentation.shared.effects

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.labajada.app.domain.model.Cart
import com.labajada.app.domain.model.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val RojoHuariqueBase = Color(0xFFD32F2F)
private val RojoOscuroEtiqueta = Color(0xFF9A0007)
private val TextoNegroAbsoluto = Color(0xFF212121)
private val TextoGrisBoucher = Color(0xFF6B7280)
private val FondoTotalGris = Color(0xFFF9FAFB)
private val BordeGrisClaro = Color(0xFFE5E7EB)

@Composable
fun OrderSuccessDialog(
    cart: Cart?,
    order: Order?,
    buyerName: String,
    onDismiss: () -> Unit
) {
    val fecha = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es")).format(Date()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 620.dp) // Limita la altura máxima en pantallas grandes
                .padding(vertical = 16.dp)
                .border(1.dp, BordeGrisClaro, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            // Se fuerza intrinsic bounds asignando peso correcto a los hijos
            Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f)) {

                // 1. CABECERA (Fija)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RojoHuariqueBase)
                        .padding(vertical = 24.dp, horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White, // Cambiado a blanco para contraste en fondo rojo
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "LA BAJADA • APP OFICIAL",
                        fontSize = 11.sp,
                        letterSpacing = 2.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "¡Pedido Confirmado!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = RojoOscuroEtiqueta,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = order?.id?.take(8)?.uppercase()?.let { "OP. $it" } ?: "OP. XXXXXXXX",
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    Text(
                        "RESUMEN DE COMPRA",
                        fontSize = 11.sp,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoHuariqueBase
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    BoletaRow(label = "Cliente", value = buyerName)
                    BoletaRow(label = "Local", value = cart?.restaurantName ?: "-")
                    BoletaRow(label = "Fecha de emisión", value = fecha)
                    BoletaRow(
                        label = "Método de entrega",
                        value = if (cart?.isDeliverySelected == true) "Delivery " else "Recojo en Local"
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    DashedDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "DETALLE DE PEDIDO",
                        fontSize = 11.sp,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoHuariqueBase
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    cart?.items?.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "${item.quantity} x ${item.dishName}",
                                fontSize = 13.sp,
                                color = TextoNegroAbsoluto,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "S/ ${String.format(Locale.US, "%.2f", item.unitPrice * item.quantity)}",
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                color = TextoNegroAbsoluto
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    DashedDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    cart?.let {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", fontSize = 13.sp, color = TextoGrisBoucher)
                            Text("S/ ${String.format(Locale.US, "%.2f", it.subtotal)}", fontSize = 13.sp, fontFamily = FontFamily.Monospace, color = TextoNegroAbsoluto)
                        }
                        if (it.isDeliverySelected && it.deliveryCost > 0) {
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Costo de envío", fontSize = 13.sp, color = TextoGrisBoucher)
                                Text("S/ ${String.format(Locale.US, "%.2f", it.deliveryCost)}", fontSize = 13.sp, fontFamily = FontFamily.Monospace, color = TextoNegroAbsoluto)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(FondoTotalGris, RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("TOTAL A PAGAR", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextoNegroAbsoluto)
                            Text(
                                "S/ ${String.format(Locale.US, "%.2f", it.total)}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = RojoHuariqueBase
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        "El restaurante ya recibió tu pedido. Puedes seguir su estado en la pestaña Pedidos.",
                        fontSize = 11.sp,
                        color = TextoGrisBoucher,
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 3. BOTÓN DE CIERRE (Fijo abajo)
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = TextoNegroAbsoluto),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            "Genial",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoletaRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = TextoGrisBoucher)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoNegroAbsoluto)
    }
}

@Composable
private fun DashedDivider() {
    Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
        drawLine(
            color = BordeGrisClaro,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
        )
    }
}