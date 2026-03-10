package com.irrigacioninteligente.irrigacionydeteccion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irrigacioninteligente.irrigacionydeteccion.data.DetectionResult
import com.irrigacioninteligente.irrigacionydeteccion.data.PlantData
import com.irrigacioninteligente.irrigacionydeteccion.data.SensorReading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class IrrigationViewModel : ViewModel() {

    // Estado de la planta detectada
    private val _plantData = MutableStateFlow<PlantData?>(null)
    val plantData: StateFlow<PlantData?> = _plantData.asStateFlow()

    // Resultado de detección (Modelo ML Real)
    private val _detectionResult = MutableStateFlow<DetectionResult?>(null)
    val detectionResult: StateFlow<DetectionResult?> = _detectionResult.asStateFlow()

    // Lista de lecturas de sensores
    private val _sensorReadings = MutableStateFlow<List<SensorReading>>(emptyList())
    val sensorReadings: StateFlow<List<SensorReading>> = _sensorReadings.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Mensaje de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Propiedades para la UI basadas en el resultado de la IA
    private val _classLabel = MutableStateFlow("")
    val classLabel: StateFlow<String> = _classLabel

    private val _confidence = MutableStateFlow(0)
    val confidence: StateFlow<Int> = _confidence

    private val _plantStatus = MutableStateFlow("")
    val plantStatus: StateFlow<String> = _plantStatus

    init {
        loadSampleData()
    }

    /**
     * Establece el resultado de detección real obtenido desde el TFLiteHelper.
     * Se llama desde la UI (CameraCaptureScreen) después de procesar la imagen.
     */
    fun updateDetectionResults(label: String, confidenceScore: Float, status: String) {
        _classLabel.value = label
        _confidence.value = confidenceScore.toInt()
        _plantStatus.value = status

        // Actualizamos también el objeto de resultado general
        _detectionResult.value = DetectionResult(
            plantName = label,
            confidence = confidenceScore,
            state = status
        )
    }

    /**
     * Carga datos iniciales para la interfaz.
     */
    private fun loadSampleData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val readings = listOf(
                    SensorReading(75f, 28f, 6.5f, "bueno", LocalDateTime.now()),
                    SensorReading(82f, 27f, 6.8f, "excelente", LocalDateTime.now().minusHours(1)),
                    SensorReading(68f, 29f, 6.2f, "bueno", LocalDateTime.now().minusHours(2)),
                    SensorReading(71f, 26f, 6.9f, "excelente", LocalDateTime.now().minusHours(3)),
                    SensorReading(79f, 28f, 6.4f, "bueno", LocalDateTime.now().minusHours(4)),
                )

                _sensorReadings.value = readings

                val plant = PlantData(
                    id = "aloe_vera_001",
                    name = "Aloe Vera",
                    readings = readings
                )

                _plantData.value = plant
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar datos: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Limpia el mensaje de error actual.
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Reinicia los estados de detección para permitir una nueva captura.
     */
    fun resetDetection() {
        _detectionResult.value = null
        _classLabel.value = ""
        _confidence.value = 0
        _plantStatus.value = ""
    }
}