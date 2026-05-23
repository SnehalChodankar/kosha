# Kosha

**Kosha** is a highly secure, offline-first password and credential manager. Designed with absolute privacy in mind, your data is encrypted directly on your device using hardware-backed AES-256 GCM encryption and never leaves your device unless explicitly exported by you. 

"Kosha" means "sheath", "layer", or "covering"—symbolizing the secure layers of encryption that protect your most sensitive data.

---

## 📥 Installation

There are two primary ways to install Kosha on your Android device: using a pre-built APK, or building it yourself from the source code.

### Option 1: Use the Pre-Built APK (Easiest)

If you simply want to install and use the app without compiling it:

1. Navigate to the `public/` directory in this repository.
2. Download the `kosha.apk` file to your Android device.
3. Open the downloaded file to install it. 
   *(Note: You may need to grant your device permission to "Install unknown apps" from your browser or file manager).*

---

### Option 2: Build the APK from Source

If you prefer to review the code and build the application yourself to ensure maximum trust and security, follow these steps:

#### Prerequisites
- **Java Development Kit (JDK) 17+**
- **Android Studio** (or the Android Command Line Tools)
- An Android device (API 26+) or an Android Emulator for testing.

#### Building via Command Line (Windows/Mac/Linux)

1. Clone the repository and navigate into the root directory:
   ```bash
   git clone https://github.com/your-username/kosha.git
   cd kosha
   ```

2. Run the Gradle build wrapper to compile the application:
   - On **Windows**:
     ```bash
     .\gradlew.bat assembleDebug
     ```
   - On **macOS/Linux**:
     ```bash
     ./gradlew assembleDebug
     ```

3. Once the build finishes successfully, you can find the generated APK at:
   `app/build/outputs/apk/debug/kosha.apk`

4. You can transfer this APK to your phone to install it, or install it directly via ADB:
   ```bash
   adb install app/build/outputs/apk/debug/kosha.apk
   ```

#### Building via Android Studio
1. Open Android Studio.
2. Select **Open an existing Android Studio project** and choose the cloned `kosha` directory.
3. Allow Gradle to sync dependencies.
4. Click the **Play (Run)** button in the top toolbar to build and install the app directly to your connected device or emulator.

---

## ✨ Core Features & Security

### ☁️ Zero Cloud Infrastructure
Kosha operates 100% offline. We do not run any servers, there are no cloud backups, no telemetry SDKs, and absolutely no tracking. You are the sole owner of your data, ensuring it cannot be breached from a remote server.

### 🛡️ SQLCipher Encrypted Database
Your data is never stored in plain text. The entire internal SQLite database is encrypted at rest using SQLCipher and military-grade AES-256 GCM encryption. The encryption keys are securely locked inside your device's hardware-backed Android Keystore.

### 👆 Biometric Integration & Auto-Lock
Seamlessly unlock your vault using your device's native fingerprint or face scanner. For maximum security, Kosha features an aggressive Auto-Lock system—the moment you minimize the app, switch to another app, or lock your phone, your vault is instantly re-secured.

### ⚡ Seamless Autofill
Kosha acts as a native Android Autofill Service. When you tap a password field in any app or web browser, Kosha gracefully prompts an "Unlock Kosha" chip. It strictly requires a biometric scan before decrypting the vault, ensuring your credentials are automatically and safely filled without opening the main app.

### 📦 Encrypted Exports
You can safely export your entire vault into a portable `.kosha` file. The exported file is heavily encrypted using the ChaCha20-Poly1305 algorithm and a uniquely generated 6-digit PIN, allowing you to securely transfer your passwords to another device without relying on cloud sync.

## 🗂️ Project Structure
- `app/src/main/java/com/locker/`: Core application logic (ViewModels, UI screens, Database layer).
- `app/src/main/res/`: Application resources (Themes, Drawables, Layouts).
- `public/`: Pre-built APKs for easy download and distribution.
