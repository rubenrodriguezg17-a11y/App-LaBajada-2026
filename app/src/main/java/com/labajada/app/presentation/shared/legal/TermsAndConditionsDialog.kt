package com.labajada.app.presentation.shared.legal

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
fun TermsAndConditionsDialog(
    isSeller: Boolean,
    onDismiss: () -> Unit,
    onAccept: (() -> Unit)? = null  // null = solo lectura (desde Configuración)
) {
    val content = if (isSeller) TermsContent.sellerTerms else TermsContent.buyerTerms
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
                Text(
                    text = if (isSeller) "Términos para Negocios" else "Términos para Compradores",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF263238)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = content,
                    fontSize = 13.sp,
                    color = Color(0xFF424242),
                    lineHeight = 19.sp,
                    modifier = Modifier.weight(1f).verticalScroll(scrollState)
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (onAccept != null) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Aceptar y continuar", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else {
                    TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}