package com.irrigacioninteligente.irrigacionydeteccion.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class TFLiteHelper(private val context: Context) {

    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(0.4f) // Umbral de confianza del 40%
            .setMaxResults(1)
            .build()

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                "aloe_vera_model.tflite", // Archivo en carpeta assets
                optionsBuilder
            )
            Log.d("TFLiteHelper", "✅ Modelo cargado correctamente")
        } catch (e: Exception) {
            Log.e("TFLiteHelper", "❌ Error al cargar modelo: ${e.message}")
        }
    }

    fun classifyImage(bitmap: Bitmap): List<Classifications>? {
        if (imageClassifier == null) return null

        return try {
            // Configuración que coincide con tu entrenamiento de Python (150x150 y rescale 1/255)
            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(150, 150, ResizeOp.ResizeMethod.BILINEAR))
                .add(NormalizeOp(0f, 255f)) // Normaliza píxeles al rango 0.0 - 1.0
                .build()

            var tensorImage = TensorImage.fromBitmap(bitmap)
            tensorImage = imageProcessor.process(tensorImage)

            val results = imageClassifier?.classify(tensorImage)

            // Log de depuración para ver el índice real que devuelve la red neuronal
            results?.forEach { classification ->
                val top = classification.categories.firstOrNull()
                if (top != null) {
                    Log.d("TFLiteHelper", "🌿 IA Detectó Índice: ${top.index} | Label: '${top.label}' | Confianza: ${top.score * 100}%")
                }
            }

            results
        } catch (e: Exception) {
            Log.e("TFLiteHelper", "❌ Error en clasificación: ${e.message}")
            null
        }
    }

    fun release() {
        imageClassifier?.close()
        Log.d("TFLiteHelper", "🔌 Recursos liberados")
    }
}