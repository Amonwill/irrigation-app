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
            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(150, 150, ResizeOp.ResizeMethod.BILINEAR)) // Tamaño de tu red
                .add(NormalizeOp(0f, 255f)) // Normalización rescale=1./255
                .build()

            var tensorImage = TensorImage.fromBitmap(bitmap)
            tensorImage = imageProcessor.process(tensorImage)

            val results = imageClassifier?.classify(tensorImage)

            // Log para depurar qué índice y confianza está arrojando realmente
            results?.forEach { classification ->
                val top = classification.categories.firstOrNull()
                if (top != null) {
                    Log.d("TFLiteHelper", "🌿 IA Índice: ${top.index} | Confianza: ${top.score * 100}%")
                }
            }
            results
        } catch (e: Exception) {
            Log.e("TFLiteHelper", "❌ Error: ${e.message}")
            null
        }
    }

    fun release() {
        imageClassifier?.close()
        Log.d("TFLiteHelper", "🔌 Recursos liberados")
    }
}