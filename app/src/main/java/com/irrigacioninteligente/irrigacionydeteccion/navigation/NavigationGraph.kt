package com.irrigacioninteligente.irrigacionydeteccion.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.irrigacioninteligente.irrigacionydeteccion.ui.screens.CameraCaptureScreen
import com.irrigacioninteligente.irrigacionydeteccion.ui.screens.DataHistoryScreen
import com.irrigacioninteligente.irrigacionydeteccion.ui.screens.DetectionResultScreen
import com.irrigacioninteligente.irrigacionydeteccion.ui.screens.MainMenuScreen
import com.irrigacioninteligente.irrigacionydeteccion.ui.screens.SplashScreen
import com.irrigacioninteligente.irrigacionydeteccion.utils.Constants

@Composable
fun IrrigationNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Constants.ROUTE_SPLASH
    ) {
        // Splash Screen
        composable(Constants.ROUTE_SPLASH) {
            SplashScreen(
                onConnectClick = {
                    navController.navigate(Constants.ROUTE_MAIN_MENU) {
                        popUpTo(Constants.ROUTE_SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Main Menu Screen
        composable(Constants.ROUTE_MAIN_MENU) {
            MainMenuScreen(
                onDetectClick = {
                    navController.navigate(Constants.ROUTE_CAMERA)
                },
                onHistoryClick = {
                    navController.navigate(Constants.ROUTE_DATA_HISTORY)
                },
                onDisconnectClick = {
                    navController.navigate(Constants.ROUTE_SPLASH) {
                        popUpTo(Constants.ROUTE_MAIN_MENU) { inclusive = true }
                    }
                }
            )
        }

        // Data History Screen
        composable(Constants.ROUTE_DATA_HISTORY) {
            DataHistoryScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Camera Capture Screen
        composable(Constants.ROUTE_CAMERA) {
            CameraCaptureScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onTakePhotoClick = {
                    navController.navigate(Constants.ROUTE_DETECTION_RESULT)
                }
            )
        }

        // Detection Result Screen
        composable(Constants.ROUTE_DETECTION_RESULT) {
            DetectionResultScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onRetakeClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}