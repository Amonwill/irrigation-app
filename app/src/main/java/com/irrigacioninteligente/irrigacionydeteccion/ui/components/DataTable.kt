package com.irrigacioninteligente.irrigacionydeteccion.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irrigacioninteligente.irrigacionydeteccion.firebase.FirebaseManager

@Composable
fun DataTable(modifier: Modifier = Modifier) {
    val headers = listOf("HUMEDAD", "TEMPERATURA", "PH", "ESTADO", "FECHA", "HORA")
    val telemetryData = remember { mutableStateOf<List<List<String>>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }

    // Obtener datos de Firebase al cargar
    LaunchedEffect(Unit) {
        FirebaseManager.getAllTelemetry { data ->
            if (data.isNotEmpty()) {
                val rows = data.mapNotNull { telemetry ->
                    try {
                        val humedad = (telemetry["Humedad"] as? Number)?.toFloat()?.toString() ?: "N/A"
                        val temperatura = (telemetry["Temperatura"] as? Number)?.toFloat()?.toString() ?: "N/A"
                        val pH = (telemetry["pH"] as? Number)?.toFloat()?.toString() ?: "N/A"
                        val estadoBomba = (telemetry["Estado_bomba"] as? String) ?: "N/A"
                        val timestamp = (telemetry["Ultima_actualizacion"] as? Number)?.toLong() ?: 0L

                        // Convertir timestamp a fecha y hora
                        val fecha = java.text.SimpleDateFormat("dd/MM/yy").format(java.util.Date(timestamp))
                        val hora = java.text.SimpleDateFormat("HH:mm").format(java.util.Date(timestamp))

                        listOf(humedad, temperatura, pH, estadoBomba, fecha, hora)
                    } catch (e: Exception) {
                        println("❌ Error procesando telemetría: ${e.message}")
                        null
                    }
                }
                telemetryData.value = rows
            }
            isLoading.value = false
        }
    }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        // Encabezado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE8D5FF))
                .border(1.dp, Color.Gray)
                .padding(8.dp)
        ) {
            headers.forEach { header ->
                Text(
                    text = header,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B4CE6)
                )
            }
        }

        // Mostrar estado de carga
        if (isLoading.value) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (telemetryData.value.isEmpty()) {
            // Mostrar mensaje si no hay datos
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay datos disponibles")
            }
        } else {
            // Filas de datos de Firebase
            telemetryData.value.forEach { rowData ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFCCCCCC))
                        .padding(8.dp)
                ) {
                    rowData.forEach { cell ->
                        Text(
                            text = cell,
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}