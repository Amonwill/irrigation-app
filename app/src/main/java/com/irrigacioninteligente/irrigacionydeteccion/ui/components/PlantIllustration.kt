package com.irrigacioninteligente.irrigacionydeteccion.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.irrigacioninteligente.irrigacionydeteccion.utils.Constants

@Composable
fun PlantIllustration(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cubos de agua/hielo
        Row {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(Constants.PURPLE_LIGHT),
                            shape = RoundedCornerShape(8.dp)
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Planta de Aloe Vera (representación simple)
        Box(
            modifier = Modifier
                .size(120.dp, 140.dp)
                .background(
                    color = Color(Constants.GREEN_PLANT),
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = 40.dp,
                        bottomEnd = 40.dp
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Interior claro de la planta
            Box(
                modifier = Modifier
                    .size(80.dp, 100.dp)
                    .background(
                        color = Color(0xFFB5D59A),
                        shape = RoundedCornerShape(30.dp)
                    )
            )
        }
    }
}