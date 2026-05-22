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

## 🔒 Security Features
- **Zero Cloud Infrastructure**: Kosha operates entirely offline. There are no servers, no telemetry, and no tracking.
- **Biometric Integration**: Seamlessly unlock your vault using your fingerprint, face scan, or device PIN.
- **Auto-Lock on Minimize**: The moment you switch apps or lock your phone, your vault is immediately re-secured.
- **SQLCipher**: The entire SQLite database is encrypted at rest using a randomly generated 32-byte passphrase, securely locked in the Android Keystore.
- **Encrypted Exports**: Safely export your data into a `.kosha` file. Exports are heavily encrypted using ChaCha20-Poly1305 and a uniquely generated 6-digit PIN.

## 🗂️ Project Structure
- `app/src/main/java/com/locker/`: Core application logic (ViewModels, UI screens, Database layer).
- `app/src/main/res/`: Application resources (Themes, Drawables, Layouts).
- `public/`: Pre-built APKs for easy download and distribution.
