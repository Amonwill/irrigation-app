package com.irrigacioninteligente.irrigacionydeteccion.utils

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor

class CameraManager(private val context: Context) {
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private val cameraExecutor: Executor = ContextCompat.getMainExecutor(context)

    fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        onError: (String) -> Unit
    ) {
        Log.d("CameraManager", "🔄 Iniciando cámara...")

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener(
            {
                try {
                    // Obtener el provider
                    cameraProvider = cameraProviderFuture.get()
                    Log.d("CameraManager", "✅ CameraProvider obtenido")

                    // Crear Preview
                    val preview = Preview.Builder().build()

                    // IMPORTANTE: Esperar a que PreviewView esté listo
                    previewView.previewStreamState.observe(lifecycleOwner) { state ->
                        Log.d("CameraManager", "Preview state: $state")
                    }

                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    Log.d("CameraManager", "✅ SurfaceProvider asignado")

                    // Crear ImageCapture
                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(previewView.display.rotation)
                        .build()

                    // Selector de cámara trasera
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    // Unbind all antes de bind
                    cameraProvider?.unbindAll()
                    Log.d("CameraManager", "✅ Cámaras desvinculadas")

                    // Bind
                    cameraProvider?.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture!!
                    )

                    Log.d("CameraManager", "✅ Cámara iniciada exitosamente")

                } catch (e: Exception) {
                    Log.e("CameraManager", "❌ Error: ${e.message}", e)
                    onError("Error al iniciar cámara: ${e.message}")
                }
            },
            cameraExecutor
        )
    }

    fun takePhoto(
        onPhotoTaken: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val imageCapture = imageCapture
        if (imageCapture == null) {
            Log.e("CameraManager", "❌ ImageCapture es null")
            onError("Cámara no inicializada")
            return
        }

        val photoDir = File(context.filesDir, "photos")
        if (!photoDir.exists()) {
            photoDir.mkdirs()
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val photoFile = File(photoDir, "IMG_$timeStamp.jpg")

        Log.d("CameraManager", "📸 Capturando foto...")

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputFileOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d("CameraManager", "✅ Foto guardada: ${photoFile.absolutePath}")
                    onPhotoTaken(photoFile.absolutePath)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraManager", "❌ Error al capturar: ${exception.imageCaptureError} - ${exception.message}", exception)
                    onError("Error al capturar foto: ${exception.message}")
                }
            }
        )
    }

    fun stopCamera() {
        try {
            cameraProvider?.unbindAll()
            Log.d("CameraManager", "🛑 Cámara detenida")
        } catch (e: Exception) {
            Log.e("CameraManager", "❌ Error al detener cámara: ${e.message}")
        }
    }
}