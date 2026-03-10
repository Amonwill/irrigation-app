package com.irrigacioninteligente.irrigacionydeteccion.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@Composable
fun DetectionResultScreen(
    onBackClick: () -> Unit,
    onRetakeClick: () -> Unit,
    plantName: String = "Desconocida",
    state: String = "detectada",
    confidence: Float = 0f
) {
    // Persistencia automática en Firebase al cargar el resultado real de la IA
    LaunchedEffect(Unit) {
        Log.d("DetectionResultScreen", "📊 Procesando resultado final de clasificación")

        FirebaseManager.saveDetectionResult(
            plantName = plantName,
            state = state,
            confidence = confidence
        )
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Icono representativo de éxito
            Text(
                text = "🌿",
                fontSize = 80.sp,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Tarjeta de Resultados Reales
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF7C3AED), // Color Púrpura Primario
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ Clasificación Exitosa",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nombre obtenido del Label del archivo .tflite
                    Text(
                        text = plantName.replace("_", " ").uppercase(),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.3f))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // Estado de salud detectado por la IA
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "ESTADO",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.uppercase(),
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(Color.White.copy(alpha = 0.3f))
                        )

                        // Confianza real del modelo
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "PRECISIÓN",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${String.format("%.1f", confidence)}%",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Indicador visual de confianza
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(confidence / 100f)
                                .height(8.dp)
                                .background(
                                    color = when {
                                        confidence >= 80f -> Color(0xFF4CAF50) // Verde: Alta confianza
                                        confidence >= 60f -> Color(0xFFFFC107) // Amarillo: Media
                                        else -> Color(0xFFFF5252) // Rojo: Baja
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Detalle Temporal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Detalles de Telemetría",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("Registro:", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.weight(1f))
                        Text(
                            text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Navegación
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CustomButton(
                    text = "MENU PRINCIPAL",
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f),
                    isPrimary = false
                )
                CustomButton(
                    text = "NUEVA CAPTURA",
                    onClick = onRetakeClick,
                    modifier = Modifier.weight(1f),
                    isPrimary = true
                )
            }
        }
    }
}