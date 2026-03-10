package com.irrigacioninteligente.irrigacionydeteccion.ui.screens

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.navigation.NavHostController
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.IrrigationAppBar
import com.irrigacioninteligente.irrigacionydeteccion.utils.Constants
import com.irrigacioninteligente.irrigacionydeteccion.utils.SensorManager
import kotlinx.coroutines.launch

@Composable
fun ControlScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val sensorManager = remember { SensorManager() }

    var pumpStatus by remember { mutableStateOf("inactive") }
    var soilHumidity by remember { mutableStateOf(0f) }
    var humidityThreshold by remember { mutableStateOf(40f) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var lastUpdate by remember { mutableStateOf("") }

    // Cargar estado inicial
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val result = sensorManager.getPumpStatus()
                result.onSuccess { data ->
                    pumpStatus = data.pumpStatus
                    soilHumidity = data.soilHumidity
                    humidityThreshold = data.humidityThreshold
                    lastUpdate = "Última actualización: ${System.currentTimeMillis()}"
                    Log.d("ControlScreen", "✅ Estado cargado")
                }
            } catch (e: Exception) {
                errorMessage = "Error cargando estado: ${e.message}"
            }
        }
    }

    Scaffold(
        topBar = {
            IrrigationAppBar(onBackClick = { navController.popBackStack() })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "🚿 CONTROL DE RIEGO",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(Constants.PURPLE_PRIMARY),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Control manual de la bomba de irrigación",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 30.dp)
            )

            // ===== TARJETA DE ESTADO DE SENSORES =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        "📊 ESTADO ACTUAL DE SENSORES",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(Constants.PURPLE_PRIMARY),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Humedad del suelo
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "🌱 Humedad Suelo:",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            "%.1f%%".format(soilHumidity),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                soilHumidity < humidityThreshold -> Color(0xFFFF6B6B) // Rojo
                                soilHumidity < humidityThreshold + 10 -> Color(0xFFFFC107) // Amarillo
                                else -> Color(0xFF4CAF50) // Verde
                            }
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Umbral óptimo
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "⚙️ Umbral Óptimo:",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            "%.1f%%".format(humidityThreshold),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(Constants.PURPLE_PRIMARY)
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Diferencia
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "📉 Diferencia:",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            "%.1f%%".format(humidityThreshold - soilHumidity),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(Constants.PURPLE_PRIMARY)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== CONTROL MANUAL DE BOMBA =====
            Text(
                text = "🎮 CONTROL MANUAL",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(Constants.PURPLE_PRIMARY),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 12.dp)
            )

            // Estado de la bomba
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (pumpStatus == "active") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Estado de la Bomba",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            if (pumpStatus == "active") "✅ ACTIVA" else "⛔ INACTIVA",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (pumpStatus == "active") Color(0xFF2E7D32) else Color(0xFFE65100)
                        )
                    }

                    // Indicador visual
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = if (pumpStatus == "active") Color(0xFF4CAF50) else Color(0xFFFF9800),
                                shape = RoundedCornerShape(50)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (pumpStatus == "active") "💧" else "🚫",
                            fontSize = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón Activar/Desactivar
            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null

                    scope.launch {
                        try {
                            val newState = pumpStatus != "active"
                            val result = sensorManager.controlPump(newState)

                            result.onSuccess { response ->
                                pumpStatus = response.pumpStatus
                                Log.d("ControlScreen", "💧 Bomba: ${response.message}")
                                errorMessage = response.message
                            }

                            result.onFailure { exception ->
                                errorMessage = "Error: ${exception.message}"
                                Log.e("ControlScreen", "Error controlando bomba: ${exception.message}")
                            }
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (pumpStatus == "active")
                        Color(0xFFFF6B6B) else Color(0xFF4CAF50)
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        if (pumpStatus == "active")
                            "🛑 DESACTIVAR BOMBA" else "💧 ACTIVAR BOMBA",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar error si hay
            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF3CD), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        "ℹ️ $errorMessage",
                        fontSize = 12.sp,
                        color = Color(0xFF856404)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Botón refrescar
            Button(
                onClick = {
                    isLoading = true

                    scope.launch {
                        try {
                            val result = sensorManager.getPumpStatus()
                            result.onSuccess { data ->
                                pumpStatus = data.pumpStatus
                                soilHumidity = data.soilHumidity
                                Log.d("ControlScreen", "🔄 Datos actualizados")
                            }
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(Constants.PURPLE_PRIMARY)
                ),
                enabled = !isLoading
            ) {
                Text(
                    "🔄 ACTUALIZAR",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Última actualización
            Text(
                lastUpdate,
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}