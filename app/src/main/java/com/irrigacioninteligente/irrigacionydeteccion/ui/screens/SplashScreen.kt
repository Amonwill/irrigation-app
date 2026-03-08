package com.irrigacioninteligente.irrigacionydeteccion.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import com.irrigacioninteligente.irrigacionydeteccion.R
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.CustomButton
import com.irrigacioninteligente.irrigacionydeteccion.utils.Constants

@Composable
fun SplashScreen(onConnectClick: () -> Unit) {
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

        Image(
            painter = painterResource(id = R.drawable.aloe_vera_icon),
            contentDescription = "Aloe Vera Icon",
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(80.dp))

        // Botón conectar dispositivo
        CustomButton(
            text = Constants.BTN_CONNECT_DEVICE,
            onClick = onConnectClick,
            isPrimary = true
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}