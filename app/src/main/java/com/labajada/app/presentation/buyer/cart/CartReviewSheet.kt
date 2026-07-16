package com.labajada.app.presentation.buyer.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.domain.model.Cart
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartReviewSheet(
    cart: Cart,
    isCheckingOut: Boolean,
    checkoutError: String?,
    onQuantityChange: (dishId: String, newQuantity: Int) -> Unit,
    onDeliverySelectedChange: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Tu pedido", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF212121))
            Text(cart.restaurantName, fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            HorizontalDivider(color = Color(0xFFEEEEEE))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                cart.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.dishName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                            Text(
                                "S/. ${String.format(Locale.US, "%.2f", item.unitPrice)} c/u",
                                fontSize = 12.sp,
                                color = Color(0xFF757575)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledIconButton(
                                onClick = { onQuantityChange(item.dishId, item.quantity - 1) },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFEEEEEE)),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("-", fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                            }
                            Text(
                                "${item.quantity}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            FilledIconButton(
                                onClick = { onQuantityChange(item.dishId, item.quantity + 1) },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFEEEEEE)),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("+", fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFEEEEEE))

            Text("Entrega", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = cart.isDeliverySelected,
                    onClick = { onDeliverySelectedChange(true) },
                    label = { Text("Delivery") }
                )
                FilterChip(
                    selected = !cart.isDeliverySelected,
                    onClick = { onDeliverySelectedChange(false) },
                    label = { Text("Recojo en local") }
                )
            }

            HorizontalDivider(color = Color(0xFFEEEEEE))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal:", fontSize = 14.sp, color = Color(0xFF757575))
                Text("S/. ${String.format(Locale.US, "%.2f", cart.subtotal)}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            if (cart.isDeliverySelected && cart.deliveryCost > 0) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Delivery:", fontSize = 14.sp, color = Color(0xFF757575))
                    Text("S/. ${String.format(Locale.US, "%.2f", cart.deliveryCost)}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF757575))
                Text(
                    "S/. ${String.format(Locale.US, "%.2f", cart.total)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFD32F2F)
                )
            }

            if (checkoutError != null) {
                Text(checkoutError, color = Color(0xFFD32F2F), fontSize = 13.sp)
            }

            Button(
                onClick = onConfirm,
                enabled = !isCheckingOut && cart.items.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isCheckingOut) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Confirmar pedido", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}