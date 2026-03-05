# 🌱 Irrigation App - Sistema Inteligente de Detección de Plantas

![License](https://img.shields.io/badge/license-MIT-green)
![Platform](https://img.shields.io/badge/platform-Android-brightgreen)
![Language](https://img.shields.io/badge/language-Kotlin-blue)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue)

Aplicación Android moderna para detección inteligente de plantas (Aloe Vera) con análisis de datos de sensores de humedad, temperatura y pH en tiempo real.

## 📱 Características Principales

### ✨ Pantallas Implementadas

1. **Splash Screen** - Pantalla de bienvenida con branding
2. **Main Menu** - Menú principal con opciones de navegación
3. **Camera Capture** - Captura de fotos con preview en tiempo real
4. **Detection Result** - Resultados de detección con confianza y estado
5. **Data History** - Historial de lecturas de sensores en tabla interactiva

### 🎯 Funcionalidades

- ✅ Navegación fluida entre 5 pantallas
- ✅ Sistema de componentes reutilizables
- ✅ ViewModel con gestión de estado reactiva
- ✅ Modelos de datos para sensores y plantas
- ✅ Tabla de datos con historial de lecturas
- ✅ Interfaz moderna con Material Design 3
- ✅ Arquitectura MVVM limpia y escalable

---

## 🛠️ Requisitos Previos

### Software Requerido

- **Android Studio** Hedgehog (2023.1.1) o superior
- **JDK 11** o superior
- **Android SDK** 36 (compileSdk)
- **Mínimo SDK** 24 (Android 7.0)

### Librerías Principales
```kotlin
// Compose & Material
androidx.compose:compose-bom:2024.x
androidx.compose.material3:material3

// Navigation
androidx.navigation:navigation-compose:2.7.7

// Lifecycle & ViewModel
androidx.lifecycle:lifecycle-runtime-compose:2.7.0
androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0

// Coroutines
kotlinx-coroutines-android:1.7.3
kotlinx-coroutines-core:1.7.3
```
## 📋 Instalación

1. Clonar el repositorio
bash
git clone https://github.com/Amonwill/irrigation-app.git
cd irrigation-app

2. Abrir en Android Studio
Abre Android Studio
File → Open → Selecciona la carpeta irrigation-app
Espera a que se sincronicen las dependencias

3. Compilar el Proyecto
```bash
# En Android Studio: Build → Make Project
# O desde terminal:
./gradlew assembleDebug
```
4. Ejecutar en Emulador
```bash
# En Android Studio: Run → Run 'app'
# O desde terminal:
./gradlew installDebug
adb shell am start -n com.irrigacioninteligente.irrigacionydeteccion/.MainActivity
```
## 📁 Estructura del Proyecto
```code
irrigation-app/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/irrigacioninteligente/irrigacionydeteccion/
│   │   │   │   ├── MainActivity.kt                 # Actividad principal
│   │   │   │   │
│   │   │   │   ├── data/
│   │   │   │   │   └── SensorData.kt              # Modelos de datos
│   │   │   │   │       ├── SensorReading
│   │   │   │   │       ├── DetectionResult
│   │   │   │   │       └── PlantData
│   │   │   │   │
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/                   # Pantallas principales
│   │   │   │   │   │   ├���─ SplashScreen.kt
│   │   │   │   │   │   ├── MainMenuScreen.kt
│   │   │   │   │   │   ├── DataHistoryScreen.kt
│   │   │   │   │   │   ├── CameraCaptureScreen.kt
│   │   │   │   │   │   └── DetectionResultScreen.kt
│   │   │   │   │   │
│   │   │   │   │   ├── components/                # Componentes reutilizables
│   │   │   │   │   │   ├── CustomButton.kt
│   │   │   │   │   │   ├── PlantIllustration.kt
│   │   │   │   │   │   ├── DataTable.kt
│   │   │   │   │   │   ├── CameraPreview.kt
│   │   │   │   │   │   └── IrrigationAppBar.kt
│   │   │   │   │   │
│   │   │   │   │   └── theme/                    # Tema Material Design 3
│   │   │   │   │       ├── Color.kt
│   │   │   │   │       ├── Theme.kt
│   │   │   │   │       └── Type.kt
│   │   │   │   │
│   │   │   │   ├── navigation/
│   │   │   │   │   └── NavigationGraph.kt        # Sistema de navegación
│   │   │   │   │
│   │   │   │   ├── viewmodel/
│   │   │   │   │   └── IrrigationViewModel.kt    # Gestión de estado
│   │   │   │   │
│   │   │   │   └── utils/
│   │   │   │       └── Constants.kt              # Constantes globales
│   │   │   │
│   │   │   └── res/
│   │   │       ├── drawable/
│   │   │       ├── values/
│   │   │       └── AndroidManifest.xml
│   │   │
│   │   ├── test/                                 # Tests unitarios
│   │   └── androidTest/                          # Tests de UI
│   │
│   ├── build.gradle.kts                          # Configuración del módulo
│   └── proguard-rules.pro
│
├── gradle/
│   └── libs.versions.toml                        # Version catalogs
│
├── build.gradle.kts                              # Build script raíz
├── settings.gradle.kts                           # Configuración de proyecto
└── README.md                                     # Este archivo
```
## 🏗️ Arquitectura
MVVM (Model-View-ViewModel)
```code
┌─────────────────┐
│   MainActivity  │  (View Layer)
└────────┬────────┘
         │
┌────────▼────────────────────┐
│   IrrigationNavGraph         │  (Navigation)
├──────────────────────────────┤
│  - SplashScreen              │
│  - MainMenuScreen            │
│  - DataHistoryScreen         │
│  - CameraCaptureScreen       │
│  - DetectionResultScreen     │
└────────┬─────────────────────┘
         │
┌────────▼──────────────────────┐
│  IrrigationViewModel          │  (ViewModel Layer)
├───────────────────────────────┤
│  - plantData: StateFlow       │
│  - sensorReadings: StateFlow  │
│  - detectionResult: StateFlow │
│  - isLoading: StateFlow       │
└────────┬──────────────────────┘
         │
┌────────▼─────────────────────┐
│   Data Models                 │  (Model Layer)
├──────────────────────────────┤
│  - SensorReading             │
│  - DetectionResult           │
│  - PlantData                 │
└──────────────────────────────┘
```
## 📊 Componentes Principales
# CustomButton
Botón reutilizable con dos estilos (primario/secundario)

```Kotlin
CustomButton(
    text = "Conectar",
    onClick = { /* acción */ },
    isPrimary = true
)
```

