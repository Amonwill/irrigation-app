package com.irrigacioninteligente.irrigacionydeteccion.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.CustomButton
import com.irrigacioninteligente.irrigacionydeteccion.ui.components.IrrigationAppBar
import com.irrigacioninteligente.irrigacionydeteccion.utils.CameraManager
import com.irrigacioninteligente.irrigacionydeteccion.utils.TFLiteHelper
import java.io.File

@Composable
fun CameraCaptureScreen(
    onBackClick: () -> Unit,
    onDetectionComplete: (String, Int, String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraManager = remember { CameraManager(context) }
    val tfLiteHelper = remember { TFLiteHelper(context) }
    val showPermissionDialog = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val isProcessing = remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            showPermissionDialog.value = true
        }
    }

    LaunchedEffect(Unit) {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(permission)
        }
    }

    if (showPermissionDialog.value) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog.value = false },
            title = { Text("Permiso de Cámara") },
            text = { Text("Se necesita acceso a la cámara para usar esta función.") },
            confirmButton = {
                CustomButton(
                    text = "Entendido",
                    onClick = { showPermissionDialog.value = false }
                )
            }
        )
    }

    if (errorMessage.value != null) {
        AlertDialog(
            onDismissRequest = { errorMessage.value = null },
            title = { Text("Error") },
            text = { Text(errorMessage.value ?: "") },
            confirmButton = {
                CustomButton(
                    text = "OK",
                    onClick = { errorMessage.value = null }
                )
            }
        )
    }

    Scaffold(
        topBar = {
            IrrigationAppBar(onBackClick = onBackClick)
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
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .background(Color.Black)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            cameraManager.startCamera(
                                previewView = this,
                                lifecycleOwner = lifecycleOwner,
                                onError = { error ->
                                    errorMessage.value = error
                                }
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
            ) {
                CustomButton(
                    text = "ATRÁS",
                    onClick = {
                        cameraManager.stopCamera()
                        tfLiteHelper.release()
                        onBackClick()
                    },
                    modifier = Modifier.weight(1f),
                    isPrimary = false,
                    enabled = !isProcessing.value
                )

                Spacer(modifier = Modifier.width(8.dp))

                CustomButton(
                    text = if (isProcessing.value) "PROCESANDO..." else "TOMAR FOTO",
                    onClick = {
                        if (!isProcessing.value) {
                            isProcessing.value = true
                            cameraManager.takePhoto(
                                onPhotoTaken = { filePath ->
                                    val imageFile = File(filePath)
                                    tfLiteHelper.detectPlantFromFile(
                                        imageFile,
                                        onSuccess = { result ->
                                            onDetectionComplete(
                                                result.classLabel,
                                                result.confidence,
                                                result.status
                                            )
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
                        }
                    },
                    modifier = Modifier.weight(1f),
                    isPrimary = true,
                    enabled = !isProcessing.value
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}