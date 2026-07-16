package com.labajada.app.presentation.shared.support

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun SupportDialog(
    isSeller: Boolean,
    onDismiss: () -> Unit
) {
    val faqList = if (isSeller) SupportContent.sellerFaq else SupportContent.buyerFaq
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.94f).fillMaxHeight(0.85f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Text("Ayuda y preguntas frecuentes", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF263238))
                Spacer(modifier = Modifier.height(12.dp))

                Column(modifier = Modifier.weight(1f).verticalScroll(scrollState)) {
                    faqList.forEach { entry ->
                        FaqItem(entry)
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("¿No encontraste tu respuesta?", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                    Text(
                        "Escríbenos a ${SupportContent.supportEmail}",
                        fontSize = 13.sp,
                        color = Color(0xFFD32F2F)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Cerrar")
                }
            }
        }
    }
}