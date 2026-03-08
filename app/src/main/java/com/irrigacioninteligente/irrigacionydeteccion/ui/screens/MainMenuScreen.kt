package com.irrigacioninteligente.irrigacionydeteccion.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.irrigacioninteligente.irrigacionydeteccion.R
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.CustomButton
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.IrrigationAppBar
import com.irrigacioninteligente.irrigacionydeteccion.utils.Constants

@Composable
fun MainMenuScreen(
    onDetectClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onDisconnectClick: () -> Unit
) {
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
            Spacer(modifier = Modifier.height(30.dp))

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