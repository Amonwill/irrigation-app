package com.irrigacioninteligente.irrigacionydeteccion.data

import java.time.LocalDateTime

// Datos del sensor
data class SensorReading(
    val humidity: Float,
    val temperature: Float,
    val ph: Float,
    val state: String,
    val dateTime: LocalDateTime
)

// Resultado de detección
data class DetectionResult(
    val plantName: String,
    val confidence: Float,
    val state: String
)

// Datos de la planta
data class PlantData(
    val id: String,
    val name: String,
    val readings: List<SensorReading> = emptyList()
)