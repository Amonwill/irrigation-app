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
import androidx.navigation.NavHostController
import com.irrigacioninteligente.irrigacionydeteccion.R
import com.irrigacioninteligente.irrigacionydeteccion.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(navController: NavHostController) {
    var plantName by remember { mutableStateOf("Aloe Vera") }
    var humidityThreshold by remember { mutableStateOf(40) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("IRRIGACION INTELIGENTE") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(Constants.PURPLE_PRIMARY),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Estado de conexión
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFD4EDDA),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        "✅ Dispositivo Conectado",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF155724)
                    )
                    Text(
                        "Planta: $plantName | Umbral: $humidityThreshold%",
                        fontSize = 12.sp,
                        color = Color(0xFF155724)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.aloe_vera_icon),
                contentDescription = "Aloe Vera Icon",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Botón detectar planta
            Button(
                onClick = {
                    navController.navigate(Constants.ROUTE_CAMERA)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(Constants.GREEN_PLANT)
                )
            ) {
                Text(
                    "📷 Detectar Planta",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón panel de control
            Button(
                onClick = {
                    navController.navigate(Constants.ROUTE_CONTROL)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00ACC1)
                )
            ) {
                Text(
                    "🎮 Panel de Control",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón historial
            Button(
                onClick = {
                    navController.navigate(Constants.ROUTE_DATA_HISTORY)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(Constants.PURPLE_PRIMARY)
                )
            ) {
                Text(
                    "📊 Historial de Datos",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón desconectar
            Button(
                onClick = {
                    Log.d("MainMenuScreen", "🔌 Desconectado")
                    navController.navigate(Constants.ROUTE_SPLASH) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(Constants.GRAY_BUTTON)
                )
            ) {
                Text(
                    Constants.BTN_DISCONNECT,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}