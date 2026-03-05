package com.irrigacioninteligente.irrigacionydeteccion.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CameraPreview(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(300.dp)
            .background(Color.White)
            .border(3.dp, Color.Black)
    ) {
        // Esquinas negras (marcadores de enfoque)
        // Esquina superior izquierda
        Box(
            modifier = Modifier
                .size(40.dp, 40.dp)
                .background(Color.Transparent)
        )
        
        // El contenido de la cámara iría aquí
    }
}