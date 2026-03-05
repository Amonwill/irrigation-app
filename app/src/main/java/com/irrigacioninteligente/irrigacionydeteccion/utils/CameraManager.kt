package com.irrigacioninteligente.irrigacionydeteccion.utils

import android.content.Context
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
import java.util.Locale
import java.util.concurrent.Executor

class CameraManager(private val context: Context) {

    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null

    fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // ImageCapture
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()

                // Selector de cámara frontal
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Unbind previous bindings
                cameraProvider?.unbindAll()

                // Bind
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                onSuccess()
            } catch (exc: Exception) {
                onError("Error al inicializar cámara: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun takePhoto(
        onPhotoTaken: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val imageCapture = imageCapture ?: return

        // Crear directorio para fotos
        val photoDir = File(context.getExternalFilesDir(null), "photos")
        if (!photoDir.exists()) {
            photoDir.mkdirs()
        }

        // Nombre del archivo
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        val photoFile = File(photoDir, "IMG_$timestamp.jpg")

        // Opciones de salida
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Capturar foto
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    onPhotoTaken(photoFile.absolutePath)
                }

                override fun onError(exc: ImageCaptureException) {
                    onError("Error al capturar foto: ${exc.message}")
                }
            }
        )
    }

    fun stopCamera() {
        cameraProvider?.unbindAll()
    }
}