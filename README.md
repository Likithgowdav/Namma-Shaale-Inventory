# Namma Shaale Inventory 🏫📦

**Namma Shaale Inventory** is a modern, professional Android application designed to streamline asset management and inventory tracking for government schools. Built with a focus on accessibility and security, the app empowers school staff to maintain high-quality educational environments.

---

## 📌 Problem Statement
Government schools often struggle with paper-based inventory management, leading to misplaced assets, lack of maintenance history, and difficulty in official auditing. **Namma Shaale Inventory** digitizes this process, providing a transparent, efficient, and professional way to track school property from procurement to disposal.

---

## 🌟 Key Features

### 🔐 Secure Cloud Authentication
- **Firebase Integration**: Secure Login and Sign-Up system using Email and Password.
- **Persistent Sessions**: Stay logged in even after closing the app for faster daily access.

### 🌍 Multilingual Support (English & Kannada)
- **Localization**: Full support for **Kannada**, ensuring inclusivity for staff in rural government schools.
- **Dynamic Toggle**: Instantly switch languages from the settings menu.

### 📊 Professional Dashboard
- **Real-time Insights**: View health statistics of school assets (Working, Needs Repair, Broken).
- **School Profile**: Integrated school information including DISE code and address.

### 📄 Inventory Management & Reporting
- **Asset Directory**: Comprehensive list of all school furniture, electronics, and supplies.
- **PDF Generation**: Export professional inventory reports with a single tap for official documentation.
- **Monthly Health Check**: Dedicated workflow for periodic audits of school infrastructure.

---

## 🚀 Advanced Features (Next-Gen Inventory)

### 📈 Condition History per Asset [LIVE]
- **Full Audit Trail**: Track every change in an asset's condition over its entire lifecycle.
- **Timeline View**: A professional vertical timeline showing exactly when and why an item's status changed.
- **Remarks & Context**: Capture detailed notes during each health check for better maintenance tracking.

---

## 🛠️ Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Modern Declarative UI)
- **Architecture**: **MVVM (Model-View-ViewModel)** + **Clean Architecture** (Data, Domain, UI layers)
- **Database**: **Room Database** (Offline-first local persistence with SQLite)
- **Dependency Injection**: **Hilt** (Google's standard for Android DI)
- **Authentication**: **Firebase Auth** (Secure cloud-based login)
- **Reporting**: **Android Native PDF API** (System-level professional document generation)
- **Asynchronous Flow**: **Kotlin Coroutines & StateFlow**

---

## 📂 Project Structure
```text
app/src/main/kotlin/com/namma_shaale/inventory/
├── data/               # Data Layer (Repositories, DAOs, Entities)
│   ├── local/          # Room DB configuration
│   └── repository/     # Single source of truth for data
├── presentation/       # UI Layer (Compose, ViewModels, UI State)
│   ├── asset/          # Add/Edit Asset logic
│   ├── assetlist/      # Main Directory UI
│   ├── assethistory/   # Audit timeline feature
│   ├── healthcheck/    # Monthly audit workflow
│   └── navigation/     # Jetpack Compose Navigation Graph
└── util/               # Helper classes (PDF, Camera, Formatting)
```

---
- **Cloud Service**: Firebase Authentication
- **DI Framework**: Hilt (Dependency Injection)
- **Reporting**: PDF Box for Android

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Iguana or newer
- JDK 17
- A Firebase project with `google-services.json` placed in the `app/` directory.

### 🛠️ Installation & Setup
1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/NammaShaaleInventory.git
   ```
2. **Firebase Setup**:
   - Create a project on [Firebase Console](https://console.firebase.google.com/).
   - Add an Android App with package name `com.namma_shaale.inventory`.
   - Download `google-services.json` and place it in the `app/` directory.
3. **Build the Project**:
   Open in Android Studio and wait for Gradle sync, or run:
   ```bash
   ./gradlew build
   ```

### 🚀 Running the App
1. Connect an Android device or start an Emulator.
2. Click **Run** in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```
2. Open the project in Android Studio.
3. Sync Gradle and run the app on an emulator or physical device.

---

## 📸 Screenshots

<p align="center">
  <img src="screenshots/login.jpeg" width="30%" />
  <img src="screenshots/signup.jpeg" width="30%" />
  <img src="screenshots/addasset.jpeg" width="30%" />
</p>

<p align="center">
  <img src="screenshots/dashboard.jpeg" width="30%" />
  <img src="screenshots/assetdirectory.jpeg" width="30%" />
  <img src="screenshots/monthlyhealthcheck.jpeg" width="30%" />
</p>

<p align="center">
  <img src="screenshots/report.jpeg" width="30%" />
  <img src="screenshots/issuelog.jpeg" width="30%" />
  <img src="screenshots/repairqueue.jpeg" width="30%" />
</p>

---

---

## 🔮 Future Roadmap
- **Role-Based Access Control (RBAC)**: Implementing specific logins for Principals, Teachers, and SDMC Members to enhance security.
- **Advanced Analytics**: Dashboards showing inventory value trends and repair cost projections.
- **Offline Sync**: Automatic background synchronization with Firebase when an internet connection is restored.
- **GenAI Image Audits**: Using Google ML Kit to automatically detect and grade asset condition from photos.

---

## 📄 License
This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

---

**Developed for Namma Shaale Initiative** 🇮🇳
