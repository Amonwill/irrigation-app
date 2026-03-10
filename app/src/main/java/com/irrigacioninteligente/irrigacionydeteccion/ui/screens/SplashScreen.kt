package com.irrigacioninteligente.irrigacionydeteccion.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irrigacioninteligente.irrigacionydeteccion.R
import com.irrigacioninteligente.irrigacionydeteccion.utils.Constants
import com.irrigacioninteligente.irrigacionydeteccion.utils.MockSensorManager
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onConnectClick: () -> Unit) {
    val scope = rememberCoroutineScope()
    val mockSensorManager = remember { MockSensorManager() }

    var isConnecting by remember { mutableStateOf(false) }
    var connectionError by remember { mutableStateOf<String?>(null) }
    var humidityThreshold by remember { mutableStateOf(40.0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Título
        Text(
            text = Constants.APP_TITLE,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.aloe_vera_icon),
            contentDescription = "Aloe Vera Icon",
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Mostrar error si hay
        if (connectionError != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFF8D7DA),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
                    .padding(bottom = 20.dp)
            ) {
                Text(
                    "❌ Error: $connectionError",
                    fontSize = 12.sp,
                    color = Color(0xFF721C24)
                )
            }
        }

        // Botón conectar dispositivo
        Button(
            onClick = {
                isConnecting = true
                connectionError = null
                scope.launch {
                    try {
                        Log.d("SplashScreen", "🔌 Intentando conectar a Mock ESP32...")

                        val result = mockSensorManager.connectDevice(
                            plantName = "Aloe Vera",
                            humidityThreshold = humidityThreshold
                        )

                        result.onSuccess { response ->
                            Log.d("SplashScreen", "✅ Mock Conectado: ${response.message}")
                            isConnecting = false
                            onConnectClick() // Navega a MainMenuScreen
                        }

                        result.onFailure { exception ->
                            connectionError = exception.message
                            isConnecting = false
                            Log.e("SplashScreen", "❌ Error: ${exception.message}")
                        }
                    } catch (e: Exception) {
                        connectionError = "Error: ${e.message}"
                        isConnecting = false
                        Log.e("SplashScreen", "Error: ${e.message}")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(Constants.PURPLE_PRIMARY)
            ),
            enabled = !isConnecting
        ) {
            if (isConnecting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            } else {
                Text(
                    Constants.BTN_CONNECT_DEVICE,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}