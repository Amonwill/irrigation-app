package com.irrigacioninteligente.irrigacionydeteccion.utils

import android.util.Log
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Mock del SensorManager para simular ESP32 sin hardware real
 * Retorna datos realistas con variación aleatoria
 */
class MockSensorManager {
    companion object {
        private const val TAG = "MockSensorManager"
    }

    private var isConnected = false
    private var plantName = ""
    private var humidityThreshold = 40f

    /**
     * Simula conexión al ESP32
     */
    suspend fun connectDevice(
        plantName: String,
        humidityThreshold: Float
    ): Result<DeviceResponse> {
        return try {
            // Simular delay de conexión (500ms - 2s)
            delay(Random.nextLong(500, 2000))

            this.isConnected = true
            this.plantName = plantName
            this.humidityThreshold = humidityThreshold

            val response = DeviceResponse(
                status = "success",
                deviceConnected = true,
                plantVerified = plantName.equals("Aloe Vera", ignoreCase = true),
                message = "✅ Conectado a ESP32 Mock - Planta: $plantName"
            )

            Log.d(TAG, "✅ Mock Device Conectado: $response")
            Result.success(response)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error en Mock Connect: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Simula lectura de sensores con datos realistas
     */
    suspend fun readSensors(): Result<SensorReading> {
        return try {
            if (!isConnected) {
                throw Exception("Dispositivo no conectado")
            }

            // Simular delay de lectura (300ms - 800ms)
            delay(Random.nextLong(300, 800))

            // Generar datos realistas con variación
            val soilHumidity = Random.nextFloat() * 100 // 0-100%
            val temperature = 20f + Random.nextFloat() * 15 // 20-35°C
            val humidity = 40f + Random.nextFloat() * 40 // 40-80% humedad ambiental
            val pressure = 1010f + Random.nextFloat() * 20 // 1010-1030 hPa
            val altitude = 300f + Random.nextFloat() * 100 // 300-400m
            val pH = 6.0f + Random.nextFloat() * 2 // 6.0-8.0 pH
            val tankLevel = Random.nextFloat() * 100 // 0-100%

            val reading = SensorReading(
                soilHumidity = soilHumidity,
                temperature = temperature,
                humidity = humidity,
                pressure = pressure,
                altitude = altitude,
                pH = pH,
                tankLevel = tankLevel,
                pumpStatus = if (soilHumidity < humidityThreshold) "active" else "inactive",
                humidityThreshold = humidityThreshold,
                timestamp = System.currentTimeMillis()
            )

            Log.d(TAG, "📊 Mock Sensores Leídos:")
            Log.d(TAG, "  🌱 Humedad suelo: ${reading.soilHumidity.toInt()}%")
            Log.d(TAG, "  🌡️ Temperatura: ${reading.temperature.toInt()}°C")
            Log.d(TAG, "  💨 Humedad ambiental: ${reading.humidity.toInt()}%")
            Log.d(TAG, "  🫖 Nivel tanque: ${reading.tankLevel.toInt()}%")
            Log.d(TAG, "  💧 Bomba: ${reading.pumpStatus}")

            Result.success(reading)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error en Mock Sensors: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Simula control de bomba
     */
    suspend fun controlPump(state: Boolean): Result<PumpResponse> {
        return try {
            if (!isConnected) {
                throw Exception("Dispositivo no conectado")
            }

            delay(Random.nextLong(200, 500))

            val response = PumpResponse(
                status = "success",
                pumpStatus = if (state) "active" else "inactive",
                message = "💧 Bomba ${if (state) "activada" else "desactivada"}"
            )

            Log.d(TAG, "✅ Mock Pump Controlado: ${response.pumpStatus}")
            Result.success(response)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error en Mock Pump: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Simula obtener estado de la bomba
     */
    suspend fun getPumpStatus(): Result<SensorReading> {
        return try {
            if (!isConnected) {
                throw Exception("Dispositivo no conectado")
            }

            delay(Random.nextLong(200, 500))

            val soilHumidity = Random.nextFloat() * 100
            val reading = SensorReading(
                soilHumidity = soilHumidity,
                temperature = 0f,
                humidity = 0f,
                pressure = 0f,
                altitude = 0f,
                pH = 0f,
                tankLevel = Random.nextFloat() * 100,
                pumpStatus = if (soilHumidity < humidityThreshold) "active" else "inactive",
                humidityThreshold = humidityThreshold,
                timestamp = System.currentTimeMillis()
            )

            Log.d(TAG, "✅ Mock Pump Status: ${reading.pumpStatus}")
            Result.success(reading)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error en Mock Pump Status: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Simula obtener estado del dispositivo
     */
    suspend fun getDeviceStatus(): Result<DeviceResponse> {
        return try {
            val response = DeviceResponse(
                status = "success",
                deviceConnected = isConnected,
                plantVerified = plantName.equals("Aloe Vera", ignoreCase = true),
                message = "Dispositivo Mock - Conectado: $isConnected"
            )

            Log.d(TAG, "✅ Mock Device Status: $isConnected")
            Result.success(response)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error en Mock Device Status: ${e.message}")
            Result.failure(e)
        }
    }

    fun disconnect() {
        isConnected = false
        Log.d(TAG, "🔌 Mock Desconectado")
    }
}