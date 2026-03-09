package com.irrigacioninteligente.irrigacionydeteccion.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irrigacioninteligente.irrigacionydeteccion.R
import com.irrigacioninteligente.irrigacionydeteccion.firebase.FirebaseManager
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.CustomButton
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.IrrigationAppBar
import com.irrigacioninteligente.irrigacionydeteccion.utils.Constants

@Composable
fun MainMenuScreen(
    onDetectClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onDisconnectClick: () -> Unit
) {
    val connectionStatus = remember { mutableStateOf("Conectando...") }
    val statusColor = remember { mutableStateOf(Color.Gray) }

    // Obtener configuración de Firebase al cargar
    LaunchedEffect(Unit) {
        FirebaseManager.getConfiguration { config ->
            if (config != null) {
                connectionStatus.value = "✅ Conectado"
                statusColor.value = Color(0xFF4CAF50) // Verde
                println("✅ Configuración obtenida de Firebase")
            } else {
                connectionStatus.value = "⚠️ Sin configuración"
                statusColor.value = Color(0xFFFFC107) // Amarillo
                println("⚠️ No hay configuración en Firebase")
            }
        }
    }

    Scaffold(
        topBar = {
            IrrigationAppBar()
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

            // Estado de conexión
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estado: ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                Text(
                    text = connectionStatus.value,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor.value
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = R.drawable.aloe_vera_icon),
                contentDescription = "Aloe Vera Icon",
                modifier = Modifier.size(180.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Botón detectar planta
            CustomButton(
                text = Constants.BTN_DETECT_PLANT,
                onClick = onDetectClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                isPrimary = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón historial
            CustomButton(
                text = Constants.BTN_DATA_HISTORY,
                onClick = onHistoryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                isPrimary = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón desconectar
            CustomButton(
                text = Constants.BTN_DISCONNECT,
                onClick = onDisconnectClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                isPrimary = false
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}