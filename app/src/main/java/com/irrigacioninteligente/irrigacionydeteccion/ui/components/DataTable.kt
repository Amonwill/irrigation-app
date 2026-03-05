package com.irrigacioninteligente.irrigacionydeteccion.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DataTable(modifier: Modifier = Modifier) {
    val headers = listOf("HUMEDAD", "TEMPERATURA", "PH", "ESTADO", "FECHA", "HORA")
    val sampleData = listOf(
        listOf("75%", "28°C", "6.5", "bueno", "05/03/26", "09:30"),
        listOf("82%", "27°C", "6.8", "excelente", "05/03/26", "10:15"),
        listOf("68%", "29°C", "6.2", "bueno", "05/03/26", "11:00"),
        listOf("71%", "26°C", "6.9", "excelente", "05/03/26", "12:45"),
        listOf("79%", "28°C", "6.4", "bueno", "05/03/26", "14:20"),
    )

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        // Encabezado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE8D5FF))
                .border(1.dp, Color.Gray)
                .padding(8.dp)
        ) {
            headers.forEach { header ->
                Text(
                    text = header,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B4CE6)
                )
            }
        }

        // Filas de datos
        sampleData.forEach { rowData ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFCCCCCC))
                    .padding(8.dp)
            ) {
                rowData.forEach { cell ->
                    Text(
                        text = cell,
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}