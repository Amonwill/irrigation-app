package com.irrigacioninteligente.irrigacionydeteccion.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.CameraPreview
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.IrrigationAppBar
import com.irrigacioninteligente.irrigacionydeteccion.utils.Constants

@Composable
fun DetectionResultScreen(
    onBackClick: () -> Unit,
    onRetakeClick: () -> Unit,
    plantName: String = Constants.SAMPLE_PLANT_NAME,
    state: String = Constants.SAMPLE_STATE,
    confidence: Float = Constants.SAMPLE_CONFIDENCE
) {
    // Guardar resultado de detección en Firebase
    LaunchedEffect(Unit) {
        FirebaseManager.saveDetectionResult(
            plantName = plantName,
            state = state,
            confidence = confidence,
            timestamp = System.currentTimeMillis()
        )
        println("✅ Resultado de detección guardado: $plantName - $state (${confidence.toInt()}%)")
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
            Spacer(modifier = Modifier.height(30.dp))

            // Preview de cámara
            CameraPreview()

            Spacer(modifier = Modifier.height(30.dp))

            // Tarjeta de resultado (rosa/magenta)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(Constants.PINK_ACCENT),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Título del resultado
                    Text(
                        text = plantName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Estado
                    Text(
                        text = "Estado: $state",
                        fontSize = 12.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Confianza
                    Text(
                        text = "Confianza: ${confidence.toInt()}%",
                        fontSize = 12.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Barra de progreso de confianza
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(confidence / 100f)
                                .height(8.dp)
                                .background(Color.White, RoundedCornerShape(4.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Checkmark (✓)
                    Text(
                        text = "✓",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Botones inferiores
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
            ) {
                CustomButton(
                    text = "ATRÁS",
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f),
                    isPrimary = false
                )

                Spacer(modifier = Modifier.width(8.dp))

                CustomButton(
                    text = "TOMAR FOTO",
                    onClick = onRetakeClick,
                    modifier = Modifier.weight(1f),
                    isPrimary = true
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}