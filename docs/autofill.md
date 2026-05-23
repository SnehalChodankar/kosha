# Autofill Integration

Kosha integrates deeply with the Android OS by implementing an `AutofillService`. This allows Kosha to securely inject credentials into other applications and web browsers natively.

## How it Works
1. **Service Registration**: Kosha declares a service in `AndroidManifest.xml` with the `BIND_AUTOFILL_SERVICE` permission.
2. **Field Detection**: When a user focuses on an input field (e.g., in the Netflix app or Chrome), the Android OS sends an `onFillRequest` to Kosha containing the View hierarchy (AssistStructure).
3. **Heuristic Parsing**: Kosha scans the view nodes looking for hints like `autofillHints="password"` or `inputType="textPassword"`.
4. **Authentication Intent**: Rather than returning plaintext passwords immediately, Kosha returns a `FillResponse` containing a "Chip" (Unlock Kosha) tied to an `Authentication Intent`.
5. **Biometric Challenge**: When the user taps the chip, the OS launches `MainActivity` in a special authentication mode. The user is prompted for Biometrics.
6. **Decryption and Injection**: Once biometrics succeed, Kosha unlocks the database, searches the database for a URL or Package Name matching the requesting app, maps the username/password to the fields, and returns a `Dataset` back to the OS. The OS then natively populates the fields.

## Security Constraints
- **Package Verification**: Kosha verifies the `packageName` of the requesting app to ensure credentials are only served to legitimate targets.
- **Zero Background Processing**: Kosha does not sit in the background reading your screen; it only wakes up when the OS explicitly invokes the Autofill API on a recognized password field.
