package com.irrigacioninteligente.irrigacionydeteccion.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.File

class TFLiteHelper(private val context: Context) {

    private var isInitialized = false
    private val labels = mutableListOf<String>()

    init {
        loadLabels()
        isInitialized = labels.isNotEmpty()
    }

    private fun loadLabels() {
        try {
            val labelsInputStream = context.assets.open("labels.txt")
            val content = labelsInputStream.bufferedReader().use { it.readText() }

            // Split por newline y filtrar espacios
            val loadedLabels = content.split("\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            labels.clear()
            labels.addAll(loadedLabels)

            android.util.Log.d("TFLiteHelper", "Labels cargados: $labels (${labels.size})")

        } catch (e: Exception) {
            android.util.Log.e("TFLiteHelper", "Error cargando labels: ${e.message}")
            e.printStackTrace()
            // Fallback: labels por defecto
            labels.addAll(listOf("saludable", "requiere_agua", "enferma"))
        }
    }

    /**
     * Detectar planta desde Bitmap (simulado)
     */
    fun detectPlantFromBitmap(
        bitmap: Bitmap,
        onSuccess: (DetectionResult) -> Unit,
        onError: (String) -> Unit
    ) {
        android.util.Log.d("TFLiteHelper", "detectPlantFromBitmap: isInitialized=$isInitialized, labels.size=${labels.size}")

        if (labels.isEmpty()) {
            onError("No hay clases disponibles para clasificación")
            return
        }

        try {
            // Simular inferencia
            val predictedLabelIndex = (0 until labels.size).random()
            val predictedLabel = labels[predictedLabelIndex]
            val confidence = (70..99).random()

            android.util.Log.d("TFLiteHelper", "Predicción: $predictedLabel ($confidence%)")

            val result = DetectionResult(
                plantName = "Aloe Vera",
                classLabel = predictedLabel,
                confidence = confidence,
                status = getPlantStatus(predictedLabel, confidence.toFloat()),
                timestamp = System.currentTimeMillis()
            )

            onSuccess(result)

        } catch (e: Exception) {
            android.util.Log.e("TFLiteHelper", "Error en detección: ${e.message}")
            onError("Error en detección: ${e.message}")
        }
    }

    /**
     * Detectar planta desde archivo
     */
    fun detectPlantFromFile(
        imageFile: File,
        onSuccess: (DetectionResult) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val bitmap = android.graphics.BitmapFactory.decodeFile(imageFile.absolutePath)
            if (bitmap != null) {
                detectPlantFromBitmap(bitmap, onSuccess, onError)
            } else {
                onError("No se pudo cargar la imagen")
            }
        } catch (e: Exception) {
            onError("Error cargando archivo: ${e.message}")
        }
    }

    /**
     * Determinar estado de la planta
     */
    private fun getPlantStatus(label: String, confidence: Float): String {
        return when {
            confidence >= 85 -> when {
                label.contains("saludable", ignoreCase = true) -> "🌱 Saludable"
                label.contains("agua", ignoreCase = true) -> "💧 Requiere Agua"
                label.contains("enferma", ignoreCase = true) -> "🚨 Enfermedad Detectada"
                else -> "✓ Normal"
            }
            confidence >= 70 -> "⚠️ Revisar Estado"
            else -> "❓ Intenta de nuevo"
        }
    }

    fun release() {
        // Cleanup si es necesario
    }
}

data class DetectionResult(
    val plantName: String,
    val classLabel: String,
    val confidence: Int,
    val status: String,
    val timestamp: Long
)