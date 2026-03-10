package com.irrigacioninteligente.irrigacionydeteccion.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.irrigacioninteligente.irrigacionydeteccion.ui.screens.CameraCaptureScreen
import com.irrigacioninteligente.irrigacionydeteccion.ui.screens.ControlScreen
import com.irrigacioninteligente.irrigacionydeteccion.ui.screens.DataHistoryScreen
import com.irrigacioninteligente.irrigacionydeteccion.ui.screens.DetectionResultScreen
import com.irrigacioninteligente.irrigacionydeteccion.ui.screens.MainMenuScreen
import com.irrigacioninteligente.irrigacionydeteccion.ui.screens.SplashScreen
import com.irrigacioninteligente.irrigacionydeteccion.viewmodel.IrrigationViewModel

sealed class IrrigationScreen(val route: String) {
    object Splash : IrrigationScreen("splash")
    object MainMenu : IrrigationScreen("main_menu")
    object CameraCapture : IrrigationScreen("camera_capture")
    object Control : IrrigationScreen("control")
    object DetectionResult : IrrigationScreen("detection_result/{plantName}/{state}/{confidence}")
    object DataHistory : IrrigationScreen("data_history")
}

@Composable
fun IrrigationNavGraph(navController: NavHostController) {
    val viewModel: IrrigationViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = IrrigationScreen.Splash.route
    ) {
        // Pantalla de bienvenida
        composable(IrrigationScreen.Splash.route) {
            SplashScreen(
                onConnectClick = {
                    navController.navigate(IrrigationScreen.MainMenu.route) {
                        popUpTo(IrrigationScreen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Menú principal
        composable(IrrigationScreen.MainMenu.route) {
            MainMenuScreen(navController = navController)
        }

        // Captura de cámara
        composable(IrrigationScreen.CameraCapture.route) {
            CameraCaptureScreen(navController = navController)
        }

        // Panel de control
        composable(IrrigationScreen.Control.route) {
            ControlScreen(navController = navController)
        }

        // Resultado de detección
        composable(
            route = "detection_result/{plantName}/{state}/{confidence}",
            arguments = listOf(
                navArgument("plantName") { type = NavType.StringType },
                navArgument("state") { type = NavType.StringType },
                navArgument("confidence") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val plantName = backStackEntry.arguments?.getString("plantName") ?: "Desconocida"
            val state = backStackEntry.arguments?.getString("state") ?: "detectada"
            val confidenceStr = backStackEntry.arguments?.getString("confidence") ?: "0.0"
            val confidence = confidenceStr.toFloatOrNull() ?: 0f

            DetectionResultScreen(
                plantName = plantName,
                state = state,
                confidence = confidence,
                onBackClick = {
                    navController.popBackStack()
                },
                onRetakeClick = {
                    navController.navigate(IrrigationScreen.CameraCapture.route) {
                        popUpTo("detection_result/{plantName}/{state}/{confidence}") { inclusive = true }
                    }
                }
            )
        }

        // Historial de datos
        composable(IrrigationScreen.DataHistory.route) {
            DataHistoryScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}