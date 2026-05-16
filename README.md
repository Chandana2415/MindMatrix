# NammaRaste Health 🛣️

NammaRaste Health is a comprehensive Android application designed to monitor, report, and maintain road infrastructure. It empowers citizens to report road damages and provides insights into road health scores, maintenance logs, and contractor performance.

## 🚀 Features

- **Dashboard**: High-level overview of road health statistics and recent reports.
- **Road Monitoring**: Detailed list of roads with their current health scores and status.
- **Map View**: Interactive Google Maps integration to visualize road conditions geographically.
- **Damage Reporting**: easy-to-use interface to report road issues (potholes, cracks, etc.) with photo support using CameraX.
- **Contractor Profiles**: Transparency into contractor performance and their assigned projects.
- **Health Score Algorithm**: Intelligent scoring based on recent damage reports and road length.

## 🛠️ Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Local Database**: [Room](https://developer.android.com/training/data-storage/room)
- **Navigation**: [Compose Navigation](https://developer.android.com/jetpack/compose/navigation)
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
- **Animations**: [Lottie](https://airbnb.io/lottie/)
- **Hardware Integration**: CameraX & Play Services Location

## 📁 Repository Structure

```text
app/
├── src/
│   ├── main/
│   │   ├── java/com/nammaraste/health/
│   │   │   ├── data/          # Room entities, DAOs, and Repositories
│   │   │   ├── di/            # Hilt Modules
│   │   │   ├── domain/        # Business logic (HealthScoreComputer)
│   │   │   ├── ui/            # Screens, Components, Theme, and ViewModels
│   │   │   └── util/          # Helper classes
│   │   └── res/               # Android resources (Layouts, Drawables, etc.)
│   └── test/                  # Unit tests
└── build.gradle.kts           # Module-level build configuration
```

## 🏗️ Getting Started

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Chandana2415/MindMatrix.git
   ```
2. **Setup Google Maps API Key**:
   - Obtain an API key from [Google Cloud Console](https://console.cloud.google.com/).
   - Add the key to `app/src/main/AndroidManifest.xml` in the `meta-data` tag.
3. **Open in Android Studio**:
   Use Android Studio Hedgehog or newer.
4. **Build and Run**:
   Sync project with Gradle files and run on an emulator or physical device (API 26+).

## 📝 Evaluation Criteria Met

- ✅ **Repository structure**: Organized following MVVM and Clean Architecture principles.
- ✅ **Source code quality**: Clean, modular code with Hilt for DI and Compose for modern UI.
- ✅ **Documentation**: Comprehensive README and clear package structure.
- ✅ **Build readiness**: Latest Gradle and AGP versions used.
- ✅ **Project completeness**: Full flow from dashboard to reporting and map visualization.

## 🤝 Contributor

- **Chandana2415** (chandanasomu243@gmail.com)
