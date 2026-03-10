package com.irrigacioninteligente.irrigacionydeteccion.ui.screens

import android.Manifest
import android.content.Context
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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CameraCaptureScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    val cameraManager = remember { CameraManager(context) }
    val tfLiteHelper = remember { TFLiteHelper(context) }
    val mockSensorManager = remember { MockSensorManager() }

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

    if (showPermissionDialog.value) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog.value = false },
            title = { Text("Permiso de C��mara") },
            text = { Text("Se necesita acceso a la cámara para usar esta función.") },
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(60.dp)
                                .padding(bottom = 16.dp),
                            color = Color(Constants.PURPLE_PRIMARY)
                        )
                        Text(
                            "Analizando planta con IA...",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                Text(
                    text = "📷 CAPTURA DE PLANTA",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

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
                                    cameraManager.startCamera(this, lifecycleOwner) { errorMsg ->
                                        errorMessage.value = errorMsg
                                        Log.e("CameraCaptureScreen", "Error cámara: $errorMsg")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("Inicializando cámara...")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (errorMessage.value != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFCE4EC), shape = RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            "⚠️ ${errorMessage.value}",
                            fontSize = 12.sp,
                            color = Color(0xFFC2185B)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        isProcessing.value = true
                        errorMessage.value = null

                        scope.launch {
                            try {
                                Log.d("CameraCaptureScreen", "📸 Simulando captura de Aloe Vera...")

                                val confidence = 85f
                                Log.d("CameraCaptureScreen", "✅ ALOE VERA DETECTADA - Confianza: $confidence%")

                                val sensorResult = mockSensorManager.readSensors()

                                sensorResult.onSuccess { sensorData ->
                                    Log.d("CameraCaptureScreen", "📊 Datos Mock ESP32 leídos:")
                                    Log.d("CameraCaptureScreen", "  - Humedad: ${sensorData.soilHumidity}%")
                                    Log.d("CameraCaptureScreen", "  - Temperatura: ${sensorData.temperature}°C")
                                    Log.d("CameraCaptureScreen", "  - pH: ${sensorData.pH}")

                                    FirebaseManager.saveTelemetry(
                                        humedad = sensorData.soilHumidity,
                                        temperatura = sensorData.temperature,
                                        nivelTanque = sensorData.tankLevel,
                                        estadoBomba = sensorData.pumpStatus,
                                        pH = sensorData.pH
                                    )

                                    FirebaseManager.saveDetectionResult(
                                        plantName = "Aloe Vera",
                                        state = "saludable",
                                        confidence = confidence
                                    )

                                    Log.d("CameraCaptureScreen", "✅ Datos guardados en Firebase")

                                    navController.navigate(
                                        "detection_result/Aloe%20Vera/saludable/$confidence"
                                    )
                                }

                                sensorResult.onFailure { exception ->
                                    errorMessage.value = "Error leyendo sensores: ${exception.message}"
                                    Log.e("CameraCaptureScreen", "❌ Error Mock ESP32: ${exception.message}")
                                }

                            } catch (e: Exception) {
                                errorMessage.value = "Error: ${e.message}"
                                Log.e("CameraCaptureScreen", "Error: ${e.message}")
                            } finally {
                                isProcessing.value = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(Constants.PURPLE_PRIMARY)
                    ),
                    enabled = !isProcessing.value && cameraInitialized.value
                ) {
                    Text(
                        Constants.BTN_TAKE_PHOTO,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(Constants.GRAY_BUTTON)
                    )
                ) {
                    Text(
                        Constants.BTN_BACK,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}