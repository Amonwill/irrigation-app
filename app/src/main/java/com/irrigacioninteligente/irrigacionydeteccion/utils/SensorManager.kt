package com.irrigacioninteligente.irrigacionydeteccion.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

data class SensorReading(
    val soilHumidity: Float,
    val temperature: Float,
    val humidity: Float,
    val pressure: Float,
    val altitude: Float,
    val pH: Float,
    val tankLevel: Float,
    val pumpStatus: String,
    val humidityThreshold: Float,
    val timestamp: Long
)

data class DeviceResponse(
    val status: String,
    val deviceConnected: Boolean,
    val plantVerified: Boolean,
    val message: String
)

data class PumpResponse(
    val status: String,
    val pumpStatus: String,
    val message: String
)

class SensorManager(private val espIP: String = "192.168.4.1", private val espPort: Int = 8080) {

    private val baseUrl = "http://$espIP:$espPort/api"

    companion object {
        private const val TAG = "SensorManager"
        private const val TIMEOUT = 5000 // 5 segundos
    }

    /**
     * Conectar dispositivo al ESP32
     * @param plantName Nombre de la planta detectada (ej: "Aloe Vera")
     * @param humidityThreshold Umbral de humedad óptimo en Firebase
     */
    suspend fun connectDevice(
        plantName: String,
        humidityThreshold: Float
    ): Result<DeviceResponse> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/connect")
            val connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                connectTimeout = TIMEOUT
                readTimeout = TIMEOUT
                doOutput = true
            }

            // Preparar JSON
            val jsonBody = JSONObject().apply {
                put("plantName", plantName)
                put("humidityThreshold", humidityThreshold)
            }

            // Enviar solicitud
            connection.outputStream.write(jsonBody.toString().toByteArray())

            // Leer respuesta
            val responseCode = connection.responseCode
            val response = if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                val responseBody = connection.inputStream.bufferedReader().readText()
                val jsonResponse = JSONObject(responseBody)

                DeviceResponse(
                    status = jsonResponse.optString("status", ""),
                    deviceConnected = jsonResponse.optBoolean("deviceConnected", false),
                    plantVerified = jsonResponse.optBoolean("plantVerified", false),
                    message = jsonResponse.optString("message", "")
                )
            } else {
                throw Exception("Error HTTP: $responseCode")
            }

            connection.disconnect()
            Log.d(TAG, "✅ Dispositivo conectado: $response")
            Result.success(response)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error conectando dispositivo: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Leer todos los sensores del ESP32
     */
    suspend fun readSensors(): Result<SensorReading> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/sensors")
            val connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "GET"
                connectTimeout = TIMEOUT
                readTimeout = TIMEOUT
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseBody = connection.inputStream.bufferedReader().readText()
                val jsonResponse = JSONObject(responseBody)
                val data = jsonResponse.getJSONObject("data")

                val reading = SensorReading(
                    soilHumidity = data.optDouble("soilHumidity", 0.0).toFloat(),
                    temperature = data.optDouble("temperature", 0.0).toFloat(),
                    humidity = data.optDouble("humidity", 0.0).toFloat(),
                    pressure = data.optDouble("pressure", 0.0).toFloat(),
                    altitude = data.optDouble("altitude", 0.0).toFloat(),
                    pH = data.optDouble("pH", 0.0).toFloat(),
                    tankLevel = data.optDouble("tankLevel", 0.0).toFloat(),
                    pumpStatus = data.optString("pumpStatus", "inactive"),
                    humidityThreshold = data.optDouble("humidityThreshold", 0.0).toFloat(),
                    timestamp = jsonResponse.optLong("timestamp", System.currentTimeMillis())
                )

                connection.disconnect()
                Log.d(TAG, "📊 Sensores leídos: ${reading.soilHumidity}%")
                Result.success(reading)
            } else {
                throw Exception("Error HTTP: $responseCode")
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error leyendo sensores: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Controlar la bomba manualmente
     * @param state true para activar, false para desactivar
     */
    suspend fun controlPump(state: Boolean): Result<PumpResponse> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/pump/manual")
            val connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                connectTimeout = TIMEOUT
                readTimeout = TIMEOUT
                doOutput = true
            }

            // Preparar JSON
            val jsonBody = JSONObject().apply {
                put("state", state)
            }

            // Enviar solicitud
            connection.outputStream.write(jsonBody.toString().toByteArray())

            // Leer respuesta
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                val responseBody = connection.inputStream.bufferedReader().readText()
                val jsonResponse = JSONObject(responseBody)

                val response = PumpResponse(
                    status = jsonResponse.optString("status", ""),
                    pumpStatus = jsonResponse.optString("pumpStatus", ""),
                    message = jsonResponse.optString("message", "")
                )

                connection.disconnect()
                Log.d(TAG, "💧 Bomba controlada: $state")
                Result.success(response)
            } else {
                throw Exception("Error HTTP: $responseCode")
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error controlando bomba: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtener estado de la bomba actual
     */
    suspend fun getPumpStatus(): Result<SensorReading> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/pump/status")
            val connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "GET"
                connectTimeout = TIMEOUT
                readTimeout = TIMEOUT
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseBody = connection.inputStream.bufferedReader().readText()
                val jsonResponse = JSONObject(responseBody)

                val reading = SensorReading(
                    soilHumidity = jsonResponse.optDouble("humidity", 0.0).toFloat(),
                    temperature = 0f,
                    humidity = 0f,
                    pressure = 0f,
                    altitude = 0f,
                    pH = 0f,
                    tankLevel = 0f,
                    pumpStatus = jsonResponse.optString("pumpStatus", "inactive"),
                    humidityThreshold = jsonResponse.optDouble("humidityThreshold", 0.0).toFloat(),
                    timestamp = System.currentTimeMillis()
                )

                connection.disconnect()
                Result.success(reading)
            } else {
                throw Exception("Error HTTP: $responseCode")
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error obteniendo estado de bomba: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtener estado general del dispositivo
     */
    suspend fun getDeviceStatus(): Result<DeviceResponse> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/device/status")
            val connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "GET"
                connectTimeout = TIMEOUT
                readTimeout = TIMEOUT
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseBody = connection.inputStream.bufferedReader().readText()
                val jsonResponse = JSONObject(responseBody)

                val response = DeviceResponse(
                    status = "success",
                    deviceConnected = jsonResponse.optBoolean("deviceConnected", false),
                    plantVerified = jsonResponse.optBoolean("plantVerified", false),
                    message = ""
                )

                connection.disconnect()
                Log.d(TAG, "✅ Estado del dispositivo: $response")
                Result.success(response)
            } else {
                throw Exception("Error HTTP: $responseCode")
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error obteniendo estado del dispositivo: ${e.message}")
            Result.failure(e)
        }
    }
}