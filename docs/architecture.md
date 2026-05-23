# Architecture & Tech Stack

Kosha is designed to be a lightweight, robust, and completely offline Android application. 

## Tech Stack
- **Language**: Kotlin 1.9+
- **UI Framework**: Jetpack Compose (Material Design 3)
- **Local Database**: Room persistence library coupled with **SQLCipher** for database-level encryption.
- **Cryptography**: Android Hardware Keystore (`AndroidKeyStore`), `javax.crypto` (AES/GCM/NoPadding, ChaCha20-Poly1305).
- **Architecture Pattern**: MVVM (Model-View-ViewModel) using Android Architecture Components (`ViewModel`, `LiveData`, `StateFlow`).
- **Concurrency**: Kotlin Coroutines & Flow.

## Directory Structure
- `app/src/main/java/com/locker/`:
  - `crypto/`: Contains `CryptoManager.kt` and `BackupCryptoManager.kt` handling all Key generation, encryption, and decryption routines.
  - `data/`: Contains Room `AppDatabase`, `LockerItem` entities, and `LockerDao`.
  - `ui/`: Contains all Jetpack Compose screens (`AuthScreen`, `DashboardScreen`, `SettingsScreen`, etc.) and the `LockerViewModel`.
  - `service/`: Contains `KoshaAutofillService` handling system-wide autofill requests.
- `docs/`: Technical documentation.
- `kosha-web/`: The static HTML/CSS/JS code for the official landing page and web distribution.

## Core Principles
1. **Zero Network Calls**: There is no Retrofit, no OkHttp, and no networking permissions in the Manifest. 
2. **Ephemeral Memory**: Passwords copied to the clipboard or loaded in memory are cleared swiftly.
3. **Strict Biometric Enforcement**: Any decryption operation strictly mandates physical user presence via `BiometricPrompt`.
