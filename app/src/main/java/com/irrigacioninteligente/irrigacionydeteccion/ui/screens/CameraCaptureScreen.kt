package com.irrigacioninteligente.irrigacionydeteccion.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.irrigacioninteligente.irrigacionydeteccion.firebase.FirebaseManager
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.IrrigationAppBar
import com.irrigacioninteligente.irrigacionydeteccion.utils.CameraManager
import com.irrigacioninteligente.irrigacionydeteccion.utils.Constants
import com.irrigacioninteligente.irrigacionydeteccion.utils.MockSensorManager
import com.irrigacioninteligente.irrigacionydeteccion.utils.TFLiteHelper
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun CameraCaptureScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // Inicialización de utilidades con persistencia en la composición
    val cameraManager = remember { CameraManager(context) }
    val tfLiteHelper = remember { TFLiteHelper(context) }
    val mockSensorManager = remember { MockSensorManager() }

    val showPermissionDialog = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val isProcessing = remember { mutableStateOf(false) }
    val hasCameraAccess = remember { mutableStateOf(false) }
    val cameraInitialized = remember { mutableStateOf(false) }

    // Launcher para solicitar permisos de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraAccess.value = isGranted
        if (isGranted) {
            cameraInitialized.value = true
        } else {
            showPermissionDialog.value = true
        }
    }

    // Verificación de permisos de cámara al iniciar la pantalla
    LaunchedEffect(Unit) {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(permission)
        } else {
            hasCameraAccess.value = true
            cameraInitialized.value = true
        }
    }

    if (showPermissionDialog.value) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog.value = false },
            title = { Text("Permiso de Cámara") },
            text = { Text("Se necesita acceso a la cámara para realizar la detección de la planta.") },
            confirmButton = {
                Button(onClick = { showPermissionDialog.value = false }) {
                    Text("Entendido")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            IrrigationAppBar(onBackClick = { navController.popBackStack() })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isProcessing.value) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(Constants.PURPLE_PRIMARY))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Analizando con IA...", color = Color.Gray)
                    }
                }
            } else {
                Text(
                    text = "📷 CAPTURA DE PLANTA",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Contenedor de la vista previa de la cámara
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    if (cameraInitialized.value && hasCameraAccess.value) {
                        AndroidView(
                            factory = { ctx ->
                                PreviewView(ctx).apply {
                                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                                    cameraManager.startCamera(this, lifecycleOwner) { error ->
                                        errorMessage.value = error
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("Esperando acceso a la cámara...")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (errorMessage.value != null) {
                    Text("⚠️ ${errorMessage.value}", color = Color.Red, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Botón para capturar la foto e iniciar la clasificación real
                Button(
                    onClick = {
                        isProcessing.value = true
                        errorMessage.value = null

                        cameraManager.takePhoto(
                            onPhotoTaken = { path ->
                                scope.launch {
                                    try {
                                        // 1. Decodificar la imagen capturada
                                        val bitmap = BitmapFactory.decodeFile(path)

                                        // 2. Ejecutar la inferencia con TFLite
                                        val classifications = tfLiteHelper.classifyImage(bitmap)
                                        val topCategory = classifications?.firstOrNull()?.categories?.firstOrNull()

                                        if (topCategory != null) {
                                            // 3. Mapeo manual basado en los índices del entrenamiento
                                            // Índice 0: healthy_leaf, 1: rot, 2: rust
                                            val plantName = when(topCategory.index) {
                                                0 -> "Aloe Vera Sana"
                                                1 -> "Aloe Vera con Podredumbre"
                                                2 -> "Aloe Vera con Oxido"
                                                else -> "Planta Desconocida"
                                            }

                                            // Definir estado basado en el índice 0 (saludable)
                                            val state = if (topCategory.index == 0) "saludable" else "enferma"
                                            val confidence = topCategory.score * 100

                                            // 4. Obtener datos de sensores del Mock
                                            val sensorResult = mockSensorManager.readSensors()

                                            sensorResult.onSuccess { sensorData ->
                                                // 5. Persistir telemetría y resultados en Firebase
                                                FirebaseManager.saveTelemetry(
                                                    humedad = sensorData.soilHumidity,
                                                    temperatura = sensorData.temperature,
                                                    nivelTanque = sensorData.tankLevel,
                                                    estadoBomba = sensorData.pumpStatus,
                                                    pH = sensorData.pH
                                                )

                                                FirebaseManager.saveDetectionResult(
                                                    plantName = plantName,
                                                    state = state,
                                                    confidence = confidence
                                                )

                                                // 6. Navegar a la pantalla de resultados con datos procesados
                                                navController.navigate("detection_result/$plantName/$state/$confidence")
                                            }
                                        } else {
                                            errorMessage.value = "La IA no pudo identificar la planta"
                                        }
                                    } catch (e: Exception) {
                                        errorMessage.value = "Error en procesamiento: ${e.message}"
                                    } finally {
                                        isProcessing.value = false
                                        // Eliminar archivo temporal para optimizar almacenamiento
                                        File(path).delete()
                                    }
                                }
                            },
                            onError = { error ->
                                errorMessage.value = error
                                isProcessing.value = false
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(Constants.PURPLE_PRIMARY)),
                    enabled = !isProcessing.value && cameraInitialized.value
                ) {
                    Text(Constants.BTN_TAKE_PHOTO, color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(Constants.GRAY_BUTTON))
                ) {
                    Text(Constants.BTN_BACK, color = Color.White)
                }
            }
        }
    }
}