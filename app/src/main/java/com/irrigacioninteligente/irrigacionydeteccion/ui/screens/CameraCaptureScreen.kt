package com.irrigacioninteligente.irrigacionydeteccion.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.IrrigationAppBar
import com.irrigacioninteligente.irrigacionydeteccion.utils.CameraManager
import com.irrigacioninteligente.irrigacionydeteccion.utils.TFLiteHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CameraCaptureScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraManager = remember { CameraManager(context) }
    val tfLiteHelper = remember { TFLiteHelper(context) }
    val showPermissionDialog = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val isProcessing = remember { mutableStateOf(false) }
    val hasCameraAccess = remember { mutableStateOf(false) }
    val cameraInitialized = remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("CameraCaptureScreen", "📋 Permiso otorgado: $isGranted")
        hasCameraAccess.value = isGranted
        if (isGranted) {
            cameraInitialized.value = true
            Log.d("CameraCaptureScreen", "✅ Cámara lista para inicializar")
        } else {
            showPermissionDialog.value = true
        }
    }

    LaunchedEffect(Unit) {
        val permission = Manifest.permission.CAMERA
        val permissionStatus = ContextCompat.checkSelfPermission(context, permission)

        Log.d("CameraCaptureScreen", "🔍 Estado permiso: $permissionStatus")

        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            Log.d("CameraCaptureScreen", "❌ Permiso no concedido, solicitando...")
            permissionLauncher.launch(permission)
        } else {
            Log.d("CameraCaptureScreen", "✅ Permiso ya concedido")
            hasCameraAccess.value = true
            cameraInitialized.value = true
        }
    }

    // Dialog de permisos
    if (showPermissionDialog.value) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog.value = false },
            title = { Text("Permiso de Cámara") },
            text = { Text("Se necesita acceso a la cámara para usar esta función.") },
            confirmButton = {
                Button(
                    onClick = { showPermissionDialog.value = false }
                ) {
                    Text("Entendido")
                }
            }
        )
    }

    // Dialog de errores
    if (errorMessage.value != null) {
        AlertDialog(
            onDismissRequest = { errorMessage.value = null },
            title = { Text("Error") },
            text = { Text(errorMessage.value ?: "") },
            confirmButton = {
                Button(
                    onClick = { errorMessage.value = null }
                ) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        IrrigationAppBar(
            title = "IRRIGACION INTELIGENTE",
            onBackClick = { navController.popBackStack() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Preview de cámara o simulado
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(9f / 16f)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1a1a1a)),
            contentAlignment = Alignment.Center
        ) {
            if (hasCameraAccess.value && cameraInitialized.value) {
                Log.d("CameraCaptureScreen", "🏭 Renderizando PreviewView real")

                // Preview de cámara real
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        Log.d("CameraCaptureScreen", "🔄 Creando PreviewView...")
                        PreviewView(ctx).apply {
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                            Log.d("CameraCaptureScreen", "⚙️ PreviewView configurado")

                            cameraManager.startCamera(
                                previewView = this,
                                lifecycleOwner = lifecycleOwner,
                                onError = { error ->
                                    Log.e("CameraCaptureScreen", "❌ Error de cámara: $error")
                                    errorMessage.value = error
                                    cameraInitialized.value = false
                                }
                            )
                        }
                    }
                )
            } else {
                // Preview simulado
                Log.d("CameraCaptureScreen", "📷 Mostrando preview simulado")

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "📷",
                        fontSize = 80.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Vista previa de cámara",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = if (!hasCameraAccess.value) {
                            "(Permiso denegado)"
                        } else {
                            "(Cámara no disponible)"
                        },
                        color = Color(0xFFAAAAAA),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            // Botón Atrás
            Button(
                onClick = {
                    cameraManager.stopCamera()
                    tfLiteHelper.release()
                    navController.popBackStack()
                },
                enabled = !isProcessing.value,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF808080),
                    disabledContainerColor = Color(0xFF999999)
                )
            ) {
                Text("ATRÁS", color = Color.White, fontSize = 14.sp)
            }

            // Botón Tomar Foto
            Button(
                onClick = {
                    if (!isProcessing.value) {
                        isProcessing.value = true

                        if (cameraInitialized.value && hasCameraAccess.value) {
                            Log.d("CameraCaptureScreen", "📸 Usando cámara real")
                            // Usar cámara real
                            cameraManager.takePhoto(
                                onPhotoTaken = { filePath ->
                                    val imageFile = File(filePath)
                                    tfLiteHelper.detectPlantFromFile(
                                        imageFile,
                                        onSuccess = { result ->
                                            Log.d("CameraCapture", "✅ Detección: ${result.classLabel}")
                                            navController.navigate("detection_result") {
                                                popUpTo("camera_capture") { inclusive = true }
                                            }
                                            isProcessing.value = false
                                            cameraManager.stopCamera()
                                        },
                                        onError = { error ->
                                            errorMessage.value = error
                                            isProcessing.value = false
                                        }
                                    )
                                },
                                onError = { error ->
                                    errorMessage.value = error
                                    isProcessing.value = false
                                }
                            )
                        } else {
                            Log.d("CameraCaptureScreen", "🎬 Usando simulación")
                            // Usar simulación
                            simulatePhotoCapture(
                                context = context,
                                tfLiteHelper = tfLiteHelper,
                                onSuccess = {
                                    navController.navigate("detection_result") {
                                        popUpTo("camera_capture") { inclusive = true }
                                    }
                                    isProcessing.value = false
                                },
                                onError = { error ->
                                    errorMessage.value = error
                                    isProcessing.value = false
                                }
                            )
                        }
                    }
                },
                enabled = !isProcessing.value,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7C3AED),
                    disabledContainerColor = Color(0xFFB8A6D0)
                )
            ) {
                if (isProcessing.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("TOMAR FOTO", color = Color.White, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

/**
 * Simula una captura de foto con detección
 */
private fun simulatePhotoCapture(
    context: Context,
    tfLiteHelper: TFLiteHelper,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    try {
        val photoDir = File(context.filesDir, "photos")
        if (!photoDir.exists()) {
            photoDir.mkdirs()
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val photoFile = File(photoDir, "IMG_$timeStamp.jpg")

        // Crear archivo dummy
        photoFile.writeText("Simulated photo captured at $timeStamp\nFor plant detection")

        Log.d("CameraCapture", "✅ Foto simulada guardada: ${photoFile.absolutePath}")

        // Realizar detección simulada
        tfLiteHelper.detectPlantFromFile(
            photoFile,
            onSuccess = { result ->
                Log.d("CameraCapture", "✅ Detección: ${result.classLabel}")
                onSuccess()
            },
            onError = { error ->
                onError(error)
            }
        )
    } catch (e: Exception) {
        Log.e("CameraCapture", "❌ Error: ${e.message}")
        onError("Error al simular captura: ${e.message}")
    }
}