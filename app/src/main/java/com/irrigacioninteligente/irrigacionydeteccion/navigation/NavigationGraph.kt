    package com.irrigacioninteligente.irrigacionydeteccion.navigation

    import androidx.compose.runtime.Composable
    import androidx.lifecycle.viewmodel.compose.viewModel
    import androidx.navigation.NavHostController
    import androidx.navigation.compose.NavHost
    import androidx.navigation.compose.composable
    import com.irrigacioninteligente.irrigacionydeteccion.ui.screens.CameraCaptureScreen
    import com.irrigacioninteligente.irrigacionydeteccion.ui.screens.DataHistoryScreen
    import com.irrigacioninteligente.irrigacionydeteccion.ui.screens.DetectionResultScreen
    import com.irrigacioninteligente.irrigacionydeteccion.ui.screens.MainMenuScreen
    import com.irrigacioninteligente.irrigacionydeteccion.ui.screens.SplashScreen
    import com.irrigacioninteligente.irrigacionydeteccion.viewmodel.IrrigationViewModel

    sealed class IrrigationScreen(val route: String) {
        object Splash : IrrigationScreen("splash")
        object MainMenu : IrrigationScreen("main_menu")
        object CameraCapture : IrrigationScreen("camera_capture")
        object DetectionResult : IrrigationScreen("detection_result")
        object DataHistory : IrrigationScreen("data_history")
    }

    @Composable
    fun IrrigationNavGraph(navController: NavHostController) {
        val viewModel: IrrigationViewModel = viewModel()

        NavHost(
            navController = navController,
            startDestination = IrrigationScreen.Splash.route
        ) {
            composable(IrrigationScreen.Splash.route) {
                SplashScreen(
                    onConnectClick = {
                        navController.navigate(IrrigationScreen.MainMenu.route) {
                            popUpTo(IrrigationScreen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(IrrigationScreen.MainMenu.route) {
                MainMenuScreen(
                    onDetectClick = {
                        navController.navigate(IrrigationScreen.CameraCapture.route)
                    },
                    onHistoryClick = {
                        navController.navigate(IrrigationScreen.DataHistory.route)
                    },
                    onDisconnectClick = {
                        navController.navigate(IrrigationScreen.Splash.route) {
                            popUpTo(0)
                        }
                    }
                )
            }

            composable(IrrigationScreen.CameraCapture.route) {
                // ✅ ACTUALIZADO: Solo requiere navController
                CameraCaptureScreen(navController = navController)
            }

            composable(IrrigationScreen.DetectionResult.route) {
                DetectionResultScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onRetakeClick = {
                        navController.navigate(IrrigationScreen.CameraCapture.route) {
                            popUpTo(IrrigationScreen.DetectionResult.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(IrrigationScreen.DataHistory.route) {
                DataHistoryScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }