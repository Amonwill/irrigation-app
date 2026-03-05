package com.irrigacioninteligente.irrigacionydeteccion.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.CustomButton
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.CameraPreview
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.IrrigationAppBar

@Composable
fun CameraCaptureScreen(
    onBackClick: () -> Unit,
    onTakePhotoClick: () -> Unit
) {
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
            Spacer(modifier = Modifier.height(40.dp))

            // Preview de cámara
            CameraPreview()

            Spacer(modifier = Modifier.height(60.dp))

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
                    onClick = onTakePhotoClick,
                    modifier = Modifier.weight(1f),
                    isPrimary = true
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}