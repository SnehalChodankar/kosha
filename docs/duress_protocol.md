# Duress Protocol & Auto-Lock

To defend against physical coercion (e.g., rubber-hose cryptanalysis) and device theft, Kosha implements rigorous environmental protections.

## 1. Inactivity Auto-Lock
Kosha monitors app state to ensure the vault never remains unlocked unattended.
- **Lifecycle Monitoring**: Utilizing Android Lifecycle hooks (`onPause`, `onStop`), Kosha tracks exactly when the app leaves the foreground.
- **Touch Tracking**: `MainActivity` intercepts `dispatchTouchEvent` to track the exact millisecond of the user's last interaction.
- **Configurable Timers**: If the user selects "Immediately", the vault locks the millisecond the app goes to the background. If "1 Minute" is selected, a Coroutine actively monitors the `lastTouchTime`. Upon expiration, the UI state is forcefully booted back to the `AuthScreen`.

## 2. The Duress Protocol (Wipeout)
If a user is forced to unlock the device under threat, they can utilize the Duress Protocol.
- **Stealth Trigger**: The main `AuthScreen` contains an invisible `detectTapGestures` trigger mapped to the Kosha logo. A long-press reveals a minimalistic, inline Duress PIN pad.
- **Execution**: 
  1. The user inputs their pre-configured Duress PIN.
  2. The user taps the normal "Unlock" button.
  3. The app verifies the hash of the Duress PIN.
  4. If matched, Kosha silently deletes the `secure_prefs` (destroying the encrypted `db_passphrase`), and forcefully deletes the physical SQLite database files (`.db`, `-wal`, `-shm`, `-journal`).
- **The Decoy**: Because the hardware Keystore requires a biometric touch to generate a new key, we cannot silently create a fake vault. Instead, Kosha switches the UI to a massive red alert: **Fatal Error 402: Encryption keys corrupted. Security protocol triggered. Vault unrecoverable.** 
- **Plausible Deniability**: This gives the user plausible deniability. The attacker believes the app's security mechanism glitched or defended itself, not that the user intentionally entered a wipe command. Once closed, the app reverts to a fresh-install state.
