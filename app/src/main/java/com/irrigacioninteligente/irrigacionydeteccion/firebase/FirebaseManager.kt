package com.irrigacioninteligente.irrigacionydeteccion.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

object FirebaseManager {
    private lateinit var database: DatabaseReference

    fun initialize() {
        database = FirebaseDatabase.getInstance().reference
    }

    // Guardar datos de telemetría
    fun saveTelemetry(
        humedad: Float,
        temperatura: Float,
        nivelTanque: Float,
        estadoBomba: String,
        pH: Float,
        timestamp: Long = System.currentTimeMillis()
    ) {
        val telemetryData = mapOf(
            "Humedad" to humedad,
            "Temperatura" to temperatura,
            "Nivel_tanque" to nivelTanque,
            "Estado_bomba" to estadoBomba,
            "pH" to pH,
            "Ultima_actualizacion" to timestamp
        )

        database.child("Telemetria").child(timestamp.toString()).setValue(telemetryData)
            .addOnSuccessListener {
                println("✅ Telemetría guardada correctamente")
            }
            .addOnFailureListener { e ->
                println("❌ Error guardando telemetría: ${e.message}")
            }
    }

    // Guardar resultado de detección (IA/ML)
    fun saveDetectionResult(
        plantName: String,
        state: String,
        confidence: Float,
        timestamp: Long = System.currentTimeMillis()
    ) {
        val detectionData = mapOf(
            "Nombre_planta" to plantName,
            "Estado" to state,
            "Confianza" to confidence,
            "Timestamp" to timestamp
        )

        database.child("Detecciones").child(timestamp.toString()).setValue(detectionData)
            .addOnSuccessListener {
                println("✅ Detección guardada: $plantName - $state (${confidence.toInt()}%)")
            }
            .addOnFailureListener { e ->
                println("❌ Error guardando detección: ${e.message}")
            }
    }

    // Obtener todas las detecciones
    fun getAllDetections(callback: (List<Map<String, Any>>) -> Unit) {
        database.child("Detecciones").get()
            .addOnSuccessListener { snapshot ->
                val detectionsList = mutableListOf<Map<String, Any>>()
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        @Suppress("UNCHECKED_CAST")
                        child.value?.let {
                            detectionsList.add(it as Map<String, Any>)
                        }
                    }
                }
                callback(detectionsList)
            }
            .addOnFailureListener { e ->
                println("❌ Error obteniendo detecciones: ${e.message}")
                callback(emptyList())
            }
    }

    // Guardar historial de aprendizaje
    fun saveLearningHistory(
        pk: String,
        humedadSuelo: Float,
        temperatura: Float,
        climaExterno: String,
        riegoEmitido: Boolean,
        nombrePlanta: String
    ) {
        val learningData = mapOf(
            "PK" to pk,
            "Timestamp" to System.currentTimeMillis().toString(),
            "Humedad_suelo" to humedadSuelo,
            "Temperatura" to temperatura,
            "Clima_externo" to climaExterno,
            "Riego_emitido" to riegoEmitido,
            "Nombre_planta" to nombrePlanta
        )

        database.child("Historial_Aprendizaje").child(pk).setValue(learningData)
            .addOnSuccessListener {
                println("✅ Historial de aprendizaje guardado")
            }
            .addOnFailureListener { e ->
                println("❌ Error guardando historial: ${e.message}")
            }
    }

    // Obtener configuración
    fun getConfiguration(callback: (Map<String, Any>?) -> Unit) {
        database.child("Configuracion").get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    callback(snapshot.value as? Map<String, Any>)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                println("❌ Error obteniendo configuración: ${e.message}")
                callback(null)
            }
    }

    // Actualizar configuración
    fun updateConfiguration(key: String, value: Any) {
        database.child("Configuracion").child(key).setValue(value)
            .addOnSuccessListener {
                println("✅ Configuración actualizada: $key = $value")
            }
            .addOnFailureListener { e ->
                println("❌ Error actualizando configuración: ${e.message}")
            }
    }

    // Obtener telemetría en tiempo real
    fun listenToTelemetry(callback: (Map<String, Any>) -> Unit) {
        database.child("Telemetria").orderByChild("Ultima_actualizacion")
            .limitToLast(1)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (child in snapshot.children) {
                            @Suppress("UNCHECKED_CAST")
                            callback(child.value as Map<String, Any>)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("❌ Error escuchando telemetría: ${error.message}")
                }
            })
    }

    // Obtener todos los datos de telemetría
    fun getAllTelemetry(callback: (List<Map<String, Any>>) -> Unit) {
        database.child("Telemetria").get()
            .addOnSuccessListener { snapshot ->
                val telemetryList = mutableListOf<Map<String, Any>>()
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        @Suppress("UNCHECKED_CAST")
                        child.value?.let {
                            telemetryList.add(it as Map<String, Any>)
                        }
                    }
                }
                callback(telemetryList)
            }
            .addOnFailureListener { e ->
                println("❌ Error obteniendo telemetría: ${e.message}")
                callback(emptyList())
            }
    }
}