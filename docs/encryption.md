# Encryption & Security Deep Dive

Kosha utilizes a layered encryption model to ensure that even if a device is rooted, stolen, or cloned, the data remains inaccessible without the user's physical biometric signature.

## 1. Primary Vault Encryption (SQLCipher)
The core database is powered by SQLCipher, an open-source extension to SQLite that provides transparent 256-bit AES encryption of database files.
- **The Passphrase**: The database is encrypted using a strong, randomly generated 256-bit passphrase (the `db_passphrase`).
- **Passphrase Storage**: The `db_passphrase` is NEVER stored in plaintext. It is encrypted and stored in Android's `SharedPreferences`.

## 2. Hardware Keystore (AES-256 GCM)
How do we protect the `db_passphrase`? We use the Android Hardware-Backed Keystore.
- A master symmetric key is generated inside the Secure Enclave / Trusted Execution Environment (TEE).
- This key cannot be extracted from the hardware, even on a rooted device.
- **Authentication Bound**: The key is generated with `.setUserAuthenticationRequired(true)`. This means the Android OS physically prevents the key from being used to decrypt the `db_passphrase` unless the user successfully scans their fingerprint or face. 
- Every time you open Kosha or trigger Autofill, the `BiometricPrompt` must succeed within a tight time window to unlock the Keystore key, decrypt the SQLCipher passphrase, and finally decrypt the database.

## 3. Export/Import Encryption (ChaCha20-Poly1305)
When a user wishes to backup their vault to a `.kosha` file, they cannot use the Keystore key (as it is bound to the specific physical device). 
- **Encryption Algorithm**: We use `ChaCha20-Poly1305`, an authenticated encryption cipher that is highly secure and immune to padding oracle attacks.
- **Key Derivation**: Kosha generates a random 6-digit PIN. This PIN, combined with a randomly generated Salt, is run through PBKDF2 (Password-Based Key Derivation Function 2) with 100,000 iterations to derive a secure 256-bit secret key.
- **Data Structure**: The export file contains the JSON-serialized vault data, the Salt, the ChaCha20 Nonce, and the MAC (Message Authentication Code) to verify data integrity upon import.
