package com.locker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.locker.viewmodel.LockerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: LockerViewModel,
    generatedExportPin: String?,
    onClearExportPin: () -> Unit,
    pendingImportUri: android.net.Uri?,
    onClearImportUri: () -> Unit,
    onManageCategoriesClick: () -> Unit,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    onPerformImport: (android.net.Uri, String) -> Unit
) {
    // Dialog states
    var showBiometricsDialog by remember { mutableStateOf(false) }
    var showAboutDialog      by remember { mutableStateOf(false) }

    val importSummary by viewModel.importSummary.collectAsState()

    // ── Dialogs ────────────────────────────────────────────────────────────
    if (showBiometricsDialog) {
        AlertDialog(
            onDismissRequest = { showBiometricsDialog = false },
            icon = { Icon(Icons.Default.Lock, contentDescription = null) },
            title = { Text("Auto-lock on Minimize") },
            text = {
                Text(
                    "When enabled, the Locker vault will automatically lock itself the moment " +
                    "you switch to another app or minimize. You'll need to re-authenticate with " +
                    "biometrics or your device PIN to regain access.\n\n" +
                    "This feature requires a lifecycle observer hook and is coming in the next update."
                )
            },
            confirmButton = {
                TextButton(onClick = { showBiometricsDialog = false }) { Text("Got it") }
            }
        )
    }

    if (generatedExportPin != null) {
        AlertDialog(
            onDismissRequest = onClearExportPin,
            icon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Export Successful") },
            text = {
                Column {
                    Text("Your vault was exported securely. To import it later, you MUST use this PIN:")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = generatedExportPin,
                        style = MaterialTheme.typography.headlineMedium.copy(letterSpacing = 8.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Write this down! It cannot be recovered.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(onClick = onClearExportPin) { Text("I've saved it") }
            }
        )
    }

    if (pendingImportUri != null) {
        var pinInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = onClearImportUri,
            icon = { Icon(Icons.Default.Download, contentDescription = null) },
            title = { Text("Import Backup") },
            text = {
                Column {
                    Text("Enter the 6-digit PIN that was generated when you exported this backup.")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { if (it.length <= 6) pinInput = it },
                        label = { Text("6-Digit PIN") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { onPerformImport(pendingImportUri, pinInput) },
                    enabled = pinInput.length == 6
                ) { Text("Decrypt & Import") }
            },
            dismissButton = {
                TextButton(onClick = onClearImportUri) { Text("Cancel") }
            }
        )
    }

    if (importSummary != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearImportSummary() },
            title = { Text("Import Complete") },
            text = {
                Text("Successfully imported passwords.\n\nInserted: ${importSummary?.first}\nSkipped (Duplicates): ${importSummary?.second}")
            },
            confirmButton = {
                Button(onClick = { viewModel.clearImportSummary() }) { Text("OK") }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            icon = { Icon(Icons.Default.Info, contentDescription = null) },
            title = { Text("About Locker") },
            text = {
                Text(
                    "Locker v1.0\n\n" +
                    "A fully offline, hardware-encrypted password vault.\n\n" +
                    "• AES-256 GCM encryption via Android Keystore\n" +
                    "• SQLCipher encrypted database at rest\n" +
                    "• No cloud. No external servers. Ever.\n\n" +
                    "Your data never leaves your device."
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text("Close") }
            }
        )
    }

    // ── Screen ─────────────────────────────────────────────────────────────
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            SettingsGroupLabel("App Settings")
            SettingsItem(
                icon = { Icon(Icons.Default.Category, contentDescription = null) },
                title = "Manage Categories",
                subtitle = "Add, edit, or remove custom categories and brands",
                onClick = onManageCategoriesClick
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            SettingsGroupLabel("Security")

            SettingsItem(
                title = "Auto-lock on Minimize",
                subtitle = "Lock vault when you switch away from the app",
                icon = { Icon(Icons.Default.Lock, contentDescription = null) },
                onClick = { showBiometricsDialog = true }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsGroupLabel("Data")

            SettingsItem(
                icon = { Icon(Icons.Default.Upload, contentDescription = null) },
                title = "Export Vault",
                subtitle = "Save an encrypted backup file to your device",
                onClick = onExportClick
            )
            SettingsItem(
                icon = { Icon(Icons.Default.Download, contentDescription = null) },
                title = "Import Vault",
                subtitle = "Restore passwords from an encrypted backup",
                onClick = onImportClick
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsGroupLabel("About")

            SettingsItem(
                title = "About Locker",
                subtitle = "Version 1.0 • Fully offline & encrypted",
                icon = { Icon(Icons.Default.Info, contentDescription = null) },
                onClick = { showAboutDialog = true }
            )
        }
    }
}

// ── Reusable sub-composables ───────────────────────────────────────────────

@Composable
private fun SettingsGroupLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge
    )
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = icon,
        trailingContent = {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    )
}
