package com.labajada.app.presentation.buyer.search.components.sectionsMenuProfile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.presentation.buyer.search.BuyerSearchViewModel

@Composable
fun HistorialProfileSection(
    viewModel: BuyerSearchViewModel,
    onSectionChange: (String) -> Unit
) {
    val historialReal by viewModel.searchHistory.collectAsState()

    Column(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onSectionChange("MENU") }
                .padding(bottom = 16.dp)
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFFD32F2F))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Volver", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Historial de búsqueda", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF212121))
            if (historialReal.isNotEmpty()) {
                Text(
                    text = "Limpiar todo",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F),
                    modifier = Modifier.clickable {
                        viewModel.borrarTodoElHistorial()  // ← corregido (L minúscula)
                        onSectionChange("MENU")
                    }
                )
            }
        }

        if (historialReal.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no tienes antojos registrados.", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(historialReal, key = { index, item -> "${item}_$index" }) { _, item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Búsqueda reciente", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = item, fontSize = 15.sp, color = Color(0xFF212121), fontWeight = FontWeight.Medium)  // ← String directo
                        }
                    }
                }
            }
        }
    }
}