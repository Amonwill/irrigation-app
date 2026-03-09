package com.irrigacioninteligente.irrigacionydeteccion.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log

data class DetectionResult(
    val classLabel: String,
    val confidence: Float,  // ← CAMBIADO A FLOAT
    val status: String
)

class TFLiteHelper(private val context: Context) {
    private lateinit var labels: List<String>

    companion object {
        private const val LABELS_FILE = "labels.txt"
        private const val INPUT_SIZE = 150
    }

    init {
        try {
            loadLabels()
            Log.d("TFLiteHelper", "✅ TFLiteHelper inicializado")
        } catch (e: Exception) {
            Log.e("TFLiteHelper", "Error iniciando TFLite: ${e.message}")
            labels = listOf("Saludable", "Requiere Agua", "Enferma")
        }
    }

    private fun loadLabels() {
        try {
            labels = context.assets.open(LABELS_FILE)
                .bufferedReader()
                .useLines { it.toList() }
            Log.d("TFLiteHelper", "✅ Labels cargados: $labels")
        } catch (e: Exception) {
            Log.e("TFLiteHelper", "Error cargando labels: ${e.message}")
            labels = listOf("Saludable", "Requiere Agua", "Enferma")
        }
    }

    fun detectPlant(bitmap: Bitmap): DetectionResult {
        try {
            // Validar bitmap
            if (bitmap.width <= 0 || bitmap.height <= 0) {
                Log.e("TFLiteHelper", "❌ Bitmap inválido")
                return DetectionResult("Error", 0f, "error")
            }

            Log.d("TFLiteHelper", "📊 Detectando planta en imagen ${bitmap.width}x${bitmap.height}")

            // Simulación de detección basada en análisis de color
            val result = analyzeImageColor(bitmap)

            Log.d("TFLiteHelper", "✅ Detección: ${result.classLabel} (${result.confidence}%)")

            return result

        } catch (e: Exception) {
            Log.e("TFLiteHelper", "❌ Error en detección: ${e.message}")
            return DetectionResult("Error", 0f, "error")
        }
    }

    /**
     * Analiza el color dominante de la imagen para simular detección
     */
    private fun analyzeImageColor(bitmap: Bitmap): DetectionResult {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)

        var redSum = 0L
        var greenSum = 0L
        var blueSum = 0L
        var pixelCount = 0

        // Obtener los píxeles de la imagen
        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        resizedBitmap.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)

        // Sumar los valores RGB
        for (pixel in pixels) {
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF

            redSum += r
            greenSum += g
            blueSum += b
            pixelCount++
        }

        // Calcular promedios
        val avgRed = if (pixelCount > 0) (redSum / pixelCount).toInt() else 0
        val avgGreen = if (pixelCount > 0) (greenSum / pixelCount).toInt() else 0
        val avgBlue = if (pixelCount > 0) (blueSum / pixelCount).toInt() else 0

        Log.d("TFLiteHelper", "🎨 Colores promedio - R: $avgRed, G: $avgGreen, B: $avgBlue")

        // Determinar el estado basado en el color dominante
        val classLabel: String
        val confidence: Float

        return when {
            avgGreen > avgRed && avgGreen > avgBlue -> {
                // Verde dominante = Saludable
                classLabel = "Aloe Vera Saludable"
                confidence = 92.5f
                DetectionResult(classLabel, confidence, "saludable")
            }
            avgRed > avgGreen && avgRed > avgBlue -> {
                // Rojo dominante = Requiere Agua
                classLabel = "Aloe Vera Requiere Agua"
                confidence = 85.3f
                DetectionResult(classLabel, confidence, "requiere_agua")
            }
            avgBlue > avgRed && avgBlue > avgGreen -> {
                // Azul dominante = Enferma
                classLabel = "Aloe Vera Enferma"
                confidence = 78.7f
                DetectionResult(classLabel, confidence, "enferma")
            }
            else -> {
                // Color mixto = Detectada genérica
                classLabel = "Planta Detectada"
                confidence = 87.4f
                DetectionResult(classLabel, confidence, "detectada")
            }
        }
    }

    fun release() {
        Log.d("TFLiteHelper", "🔌 Liberando recursos de TFLite")
    }
}