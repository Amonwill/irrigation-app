package com.irrigacioninteligente.irrigacionydeteccion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.irrigacioninteligente.irrigacionydeteccion.firebase.FirebaseManager
import com.irrigacioninteligente.irrigacionydeteccion.navigation.IrrigationNavGraph
import com.irrigacioninteligente.irrigacionydeteccion.ui.theme.IrrigacionYDeteccionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase (antes de setContent)
        FirebaseManager.initialize()

        setContent {
            IrrigacionYDeteccionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    IrrigationNavGraph(navController = navController)
                }
            }
        }
    }
}