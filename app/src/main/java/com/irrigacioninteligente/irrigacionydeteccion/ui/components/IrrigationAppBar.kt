package com.irrigacioninteligente.irrigacionydeteccion.ui.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.irrigacioninteligente.irrigacionydeteccion.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IrrigationAppBar(
    title: String = Constants.APP_TITLE,
    onBackClick: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(Constants.PURPLE_LIGHT)
        ),
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Text("←", color = Color.Black, fontSize = 20.sp)
                }
            }
        }
    )
}