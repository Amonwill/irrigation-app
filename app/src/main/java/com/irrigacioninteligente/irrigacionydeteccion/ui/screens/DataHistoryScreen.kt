package com.irrigacioninteligente.irrigacionydeteccion.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irrigacioninteligente.irrigacionydeteccion.firebase.FirebaseManager
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.CustomButton
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.IrrigationAppBar
import java.text.SimpleDateFormat
import java.util.*

data class DetectionHistoryItem(
    val plantName: String,
    val state: String,
    val confidence: Float,
    val timestamp: Long
)

@Composable
fun DataHistoryScreen(onBackClick: () -> Unit) {
    val detectionsList = remember { mutableStateOf<List<DetectionHistoryItem>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Cargar detecciones de Firebase
    LaunchedEffect(Unit) {
        Log.d("DataHistoryScreen", "📊 Cargando historial de detecciones...")
        FirebaseManager.getAllDetections { detections ->
            try {
                val items = detections.map { detection ->
                    @Suppress("UNCHECKED_CAST")
                    DetectionHistoryItem(
                        plantName = (detection["Nombre_planta"] as? String) ?: "Desconocida",
                        state = (detection["Estado"] as? String) ?: "detectada",
                        confidence = ((detection["Confianza"] as? Number)?.toFloat()) ?: 0f,
                        timestamp = (detection["Timestamp"] as? Number)?.toLong() ?: 0L
                    )
                }.sortedByDescending { it.timestamp }

                detectionsList.value = items
                isLoading.value = false
                Log.d("DataHistoryScreen", "✅ ${items.size} detecciones cargadas")
            } catch (e: Exception) {
                Log.e("DataHistoryScreen", "❌ Error procesando detecciones: ${e.message}")
                errorMessage.value = "Error al cargar datos"
                isLoading.value = false
            }
        }
    }

    Scaffold(
        topBar = {
            IrrigationAppBar(onBackClick = onBackClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Título
            Text(
                text = "📊 HISTORIAL DE DETECCIONES",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7C3AED),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Subtítulo con cantidad
            Text(
                text = "Total de detecciones: ${detectionsList.value.size}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Mostrar estado de carga
            when {
                isLoading.value -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(50.dp),
                                color = Color(0xFF7C3AED)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Cargando detecciones...", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
                errorMessage.value != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFFFEBEE),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(20.dp)
                        ) {
                            Text("⚠️ Error", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                errorMessage.value ?: "Error desconocido",
                                fontSize = 12.sp,
                                color = Color(0xFFD32F2F)
                            )
                        }
                    }
                }
                detectionsList.value.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📭", fontSize = 80.sp, modifier = Modifier.padding(bottom = 12.dp))
                            Text(
                                "No hay detecciones aún",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )
                            Text(
                                "Comienza a detectar plantas",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                else -> {
                    // Lista de detecciones
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(detectionsList.value) { detection ->
                            DetectionHistoryCard(detection)
                        }
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón volver
            CustomButton(
                text = "ATRÁS",
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                isPrimary = false
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DetectionHistoryCard(detection: DetectionHistoryItem) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US)
    val formattedDate = dateFormat.format(Date(detection.timestamp))

    // Color basado en confianza
    val confidenceColor = when {
        detection.confidence >= 80f -> Color(0xFF4CAF50) // Verde
        detection.confidence >= 60f -> Color(0xFFFFC107) // Amarillo
        else -> Color(0xFFFF5252) // Rojo
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Encabezado con nombre y estado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = detection.plantName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Estado: ${detection.state.uppercase()}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                // Badge de confianza
                Box(
                    modifier = Modifier
                        .background(
                            color = confidenceColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${String.format("%.1f", detection.confidence)}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = confidenceColor
                    )
                }
            }

            // Divisor
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = Color(0xFFEEEEEE)
            )

            // Información de fecha/hora
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FECHA Y HORA",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formattedDate,
                        fontSize = 11.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Barra de progreso mini
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(6.dp)
                        .background(
                            color = Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(3.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(detection.confidence / 100f)
                            .height(6.dp)
                            .background(
                                color = confidenceColor,
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }
    }
}