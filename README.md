# Kosha

**Kosha** is a highly secure, offline-first password and credential manager. Designed with absolute privacy in mind, your data is encrypted directly on your device using hardware-backed AES-256 GCM encryption and never leaves your device unless explicitly exported by you. 

"Kosha" means "sheath", "layer", or "covering"—symbolizing the secure layers of encryption that protect your most sensitive data.

## 📚 Deep Dive Documentation
For detailed technical breakdowns of how Kosha works under the hood, please refer to our `docs/` directory:
- [Architecture & Tech Stack](docs/architecture.md)
- [Encryption & Security Deep Dive](docs/encryption.md)
- [Autofill Integration](docs/autofill.md)
- [Duress Protocol & Auto-Lock](docs/duress_protocol.md)

---

## 📥 Installation

There are two primary ways to install Kosha on your Android device: using a pre-built APK, or building it yourself from the source code.

### Option 1: Use the Pre-Built APK (Easiest)
1. Navigate to the `kosha-web/public/` directory in this repository, or visit the live website.
2. Download the `kosha.apk` file to your Android device.
3. Open the downloaded file to install it. 

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
   - On **Windows**: `.\gradlew.bat assembleDebug`
   - On **macOS/Linux**: `./gradlew assembleDebug`

3. Once the build finishes successfully, you can find the generated APK at:
   `app/build/outputs/apk/debug/app-debug.apk`

4. You can transfer this APK to your phone to install it, or install it directly via ADB:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

---

## ✨ Core Features Quick Overview

### ☁️ Zero Cloud Infrastructure
Kosha operates 100% offline. We do not run any servers, there are no cloud backups, no telemetry SDKs, and absolutely no tracking. You are the sole owner of your data, ensuring it cannot be breached from a remote server.

### 🛡️ SQLCipher Encrypted Database
Your data is never stored in plain text. The entire internal SQLite database is encrypted at rest using SQLCipher and military-grade AES-256 GCM encryption. The encryption keys are securely locked inside your device's hardware-backed Android Keystore.

### 👆 Biometric Integration & Flexible Auto-Lock
Seamlessly unlock your vault using your device's native fingerprint or face scanner. Kosha actively monitors your screen touches and background activity to secure the vault immediately upon minimizing the app, or after 1 minute of total inactivity.

### ⚠️ Duress Protocol
As an absolute last line of defense, Kosha features an invisible Duress Trigger. If you are forced to unlock the app, a secret PIN pad can be used to instantly and irreversibly obliterate your encryption keys and data, displaying a decoy error message to the attacker.

### ⚡ Seamless Autofill
Kosha acts as a native Android Autofill Service. When you tap a password field in any app or web browser, Kosha gracefully prompts an "Unlock Kosha" chip, securely filling credentials without opening the main app.

### 📦 Encrypted Exports
Safely export your entire vault into a portable `.kosha` file. The exported file is heavily encrypted using the ChaCha20-Poly1305 algorithm and a uniquely generated 6-digit PIN.

## 🗂️ Project Structure
- `app/src/main/java/com/locker/`: Core application logic (ViewModels, UI screens, Database layer).
- `docs/`: Technical documentation deep dives.
- `kosha-web/`: The frontend static website for distributing Kosha.
