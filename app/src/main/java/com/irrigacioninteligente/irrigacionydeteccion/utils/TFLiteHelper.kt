package com.irrigacioninteligente.irrigacionydeteccion.utils

import android.content.Context
import android.util.Log
import java.io.File

data class DetectionResult(
    val classLabel: String,
    val confidence: Int,
    val status: String
)

class TFLiteHelper(private val context: Context) {
    private val classLabels = listOf(
        "🌿 Hoja Sana",
        "🦠 Pudrición",
        "🍂 Herrumbre"
    )
    private val statusMap = mapOf(
        0 to "bueno",
        1 to "malo",
        2 to "cuidado"
    )

    fun detectPlantFromFile(
        imageFile: File,
        onSuccess: (DetectionResult) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            if (!imageFile.exists()) {
                onError("Archivo no encontrado")
                return
            }

            Log.d("TFLiteHelper", "✅ Realizando detección simulada...")
            simulateDetection(onSuccess)
        } catch (e: Exception) {
            Log.e("TFLiteHelper", "❌ Error: ${e.message}")
            onError("Error: ${e.message}")
        }
    }

    private fun simulateDetection(onSuccess: (DetectionResult) -> Unit) {
        val plants = listOf(
            DetectionResult(classLabels[0], 92, statusMap[0]!!),
            DetectionResult(classLabels[1], 88, statusMap[1]!!),
            DetectionResult(classLabels[2], 85, statusMap[2]!!)
        )
        val result = plants.random()
        Log.d("TFLiteHelper", "✅ Detección: ${result.classLabel} (${result.confidence}%)")
        onSuccess(result)
    }

    fun release() {
        Log.d("TFLiteHelper", "🛑 TFLiteHelper liberado")
    }
}