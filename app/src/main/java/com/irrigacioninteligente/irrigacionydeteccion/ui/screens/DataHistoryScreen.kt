package com.irrigacioninteligente.irrigacionydeteccion.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.DataTable
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.IrrigationAppBar

@Composable
fun DataHistoryScreen(onBackClick: () -> Unit) {
    // Guardar telemetría de ejemplo cuando se carga la pantalla
    LaunchedEffect(Unit) {
        // Ejemplo de datos de telemetría (en una app real, estos vendrían de sensores)
        FirebaseManager.saveTelemetry(
            humedad = 75f,
            temperatura = 28f,
            nivelTanque = 85f,
            estadoBomba = "activa",
            pH = 6.5f
        )
        println("✅ Telemetría guardada desde DataHistoryScreen")
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
            // Título
            Text(
                text = "HISTORIAL DE DATOS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B4CE6),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tabla de datos
            DataTable(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Botón volver
            CustomButton(
                text = "ATRÁS",
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                isPrimary = true
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}