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

    // Resultado de detección
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

    // Clase etiquetada del modelo
    private val _classLabel = MutableStateFlow("")
    val classLabel: StateFlow<String> = _classLabel

    // Confianza de la predicción
    private val _confidence = MutableStateFlow(0)
    val confidence: StateFlow<Int> = _confidence

    // Estado de la planta
    private val _plantStatus = MutableStateFlow("")
    val plantStatus: StateFlow<String> = _plantStatus

    init {
        loadSampleData()
    }

    /**
     * Establecer resultado de detección del modelo ML
     */
    fun setDetectionResult(label: String, confidence: Int, status: String) {
        _classLabel.value = label
        _confidence.value = confidence
        _plantStatus.value = status
    }

    /**
     * Cargar datos de ejemplo
     */
    private fun loadSampleData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Crear lecturas de ejemplo
                val readings = listOf(
                    SensorReading(75f, 28f, 6.5f, "bueno", LocalDateTime.now()),
                    SensorReading(82f, 27f, 6.8f, "excelente", LocalDateTime.now().minusHours(1)),
                    SensorReading(68f, 29f, 6.2f, "bueno", LocalDateTime.now().minusHours(2)),
                    SensorReading(71f, 26f, 6.9f, "excelente", LocalDateTime.now().minusHours(3)),
                    SensorReading(79f, 28f, 6.4f, "bueno", LocalDateTime.now().minusHours(4)),
                )

                _sensorReadings.value = readings

                // Crear planta de ejemplo
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
     * Realizar detección de planta (simulada)
     */
    fun detectPlant() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Simular delay de procesamiento de cámara/ML
                kotlinx.coroutines.delay(1500)

                val result = DetectionResult(
                    plantName = "ALOE VERA DETECTADA",
                    confidence = 87f,
                    state = "saba"
                )

                _detectionResult.value = result
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error en la detección: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Limpiar mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Resetear resultado de detección
     */
    fun resetDetection() {
        _detectionResult.value = null
    }
}