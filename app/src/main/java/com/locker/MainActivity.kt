package com.locker

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.locker.data.db.CryptoManager
import com.locker.data.db.DatabaseProvider
import com.locker.repository.LockerRepository
import com.locker.ui.screens.AddEditScreen
import com.locker.ui.screens.AuthScreen
import com.locker.ui.screens.DashboardScreen
import com.locker.ui.screens.GeneratorScreen
import com.locker.ui.screens.SettingsScreen
import com.locker.ui.theme.LockerTheme
import com.locker.viewmodel.LockerViewModel
import java.security.SecureRandom
import javax.crypto.Cipher

import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.locker.domain.backup.BackupCryptoManager
import com.locker.ui.screens.EditCategoryScreen
import com.locker.ui.screens.ManageCategoriesScreen

enum class RootScreen {
    AUTH, MAIN_APP, ADD_EDIT, MANAGE_CATEGORIES, EDIT_CATEGORY
}

enum class BottomNavTab(val title: String) {
    VAULT("Vault"),
    GENERATOR("Generator"),
    SETTINGS("Settings")
}

class MainActivity : FragmentActivity() {
    
    // Developer bypass — set to true ONLY when testing without biometric hardware
    private val BYPASS_BIOMETRICS_FOR_TESTING = false

    private val cryptoManager = CryptoManager()
    private var currentRootScreen by mutableStateOf(RootScreen.AUTH)
    private var currentBottomTab by mutableStateOf(BottomNavTab.VAULT)
    private var lockerViewModel: LockerViewModel? = null
    private var prefilledPasswordForAdd: String? = null
    private var editingItemId: Int? = null
    private var selectedCategoryForEdit: String? = null
    private var isExternalActionActive = false

    // Callbacks for Export/Import flow
    private var generatedExportPin: String? by mutableStateOf(null)
    private var pendingImportUri: android.net.Uri? by mutableStateOf(null)

    private val exportLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
        if (uri != null) {
            val pin = BackupCryptoManager.generatePin()
            lifecycleScope.launch {
                try {
                    val encryptedData = lockerViewModel?.exportData(pin)
                    if (encryptedData != null) {
                        contentResolver.openOutputStream(uri)?.use { it.write(encryptedData) }
                        generatedExportPin = pin
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Export Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == android.app.Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                pendingImportUri = uri // Trigger PIN input dialog in UI
            }
        }
    }

    private var lastTouchTime: Long = System.currentTimeMillis()
    private var inactivityJob: kotlinx.coroutines.Job? = null

    private var showCorruptedDialog by mutableStateOf(false)

    override fun dispatchTouchEvent(ev: android.view.MotionEvent?): Boolean {
        lastTouchTime = System.currentTimeMillis()
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_SECURE,
            android.view.WindowManager.LayoutParams.FLAG_SECURE
        )

        setContent {
            LockerTheme {
                val snackbarHostState = remember { SnackbarHostState() }

                if (showCorruptedDialog) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { finish() },
                        title = { Text("Fatal Error 402", color = MaterialTheme.colorScheme.error) },
                        text = { Text("Encryption keys corrupted. Security protocol triggered. Vault unrecoverable.") },
                        confirmButton = {
                            Button(onClick = { finish() }) { Text("Close") }
                        }
                    )
                }
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    androidx.compose.animation.Crossfade(targetState = currentRootScreen, label = "RootScreen") { screen ->
                        when (screen) {
                            RootScreen.AUTH -> {
                                AuthScreen(
                                    onAuthenticateClick = { 
                                        if (BYPASS_BIOMETRICS_FOR_TESTING) {
                                            initializeDatabaseAndViewModel()
                                        } else {
                                            showBiometricPrompt() 
                                        }
                                    },
                                    onDuressTriggered = { wipeAndReset() }
                                )
                            }
                            RootScreen.MAIN_APP -> {
                                Scaffold(
                                    snackbarHost = { SnackbarHost(snackbarHostState) },
                                    bottomBar = {
                                        NavigationBar {
                                            NavigationBarItem(
                                                icon = { Icon(Icons.Default.List, contentDescription = "Vault") },
                                                label = { Text("Vault") },
                                                selected = currentBottomTab == BottomNavTab.VAULT,
                                                onClick = { currentBottomTab = BottomNavTab.VAULT }
                                            )
                                            NavigationBarItem(
                                                icon = { Icon(Icons.Default.Build, contentDescription = "Generator") },
                                                label = { Text("Generator") },
                                                selected = currentBottomTab == BottomNavTab.GENERATOR,
                                                onClick = { currentBottomTab = BottomNavTab.GENERATOR }
                                            )
                                            NavigationBarItem(
                                                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                                label = { Text("Settings") },
                                                selected = currentBottomTab == BottomNavTab.SETTINGS,
                                                onClick = { currentBottomTab = BottomNavTab.SETTINGS }
                                            )
                                        }
                                    }
                                ) { innerPadding ->
                                    Surface(modifier = Modifier.padding(innerPadding)) {
                                        androidx.compose.animation.Crossfade(targetState = currentBottomTab, label = "BottomTab") { tab ->
                                            when (tab) {
                                                BottomNavTab.VAULT -> {
                                                    lockerViewModel?.let { vm ->
                                                        DashboardScreen(
                                                            viewModel = vm,
                                                            snackbarHostState = snackbarHostState,
                                                            onAddClick = { 
                                                                editingItemId = null
                                                                currentRootScreen = RootScreen.ADD_EDIT 
                                                            },
                                                            onEditClick = { itemId ->
                                                                editingItemId = itemId
                                                                currentRootScreen = RootScreen.ADD_EDIT
                                                            }
                                                        )
                                                    }
                                                }
                                                BottomNavTab.GENERATOR -> {
                                                    GeneratorScreen(
                                                        snackbarHostState = snackbarHostState,
                                                        onUsePassword = { generatedPassword ->
                                                            prefilledPasswordForAdd = generatedPassword
                                                            currentRootScreen = RootScreen.ADD_EDIT
                                                        }
                                                    )
                                                }
                                                BottomNavTab.SETTINGS -> {
                                                    lockerViewModel?.let { vm ->
                                                        SettingsScreen(
                                                            viewModel = vm,
                                                            generatedExportPin = generatedExportPin,
                                                            onClearExportPin = { generatedExportPin = null },
                                                            pendingImportUri = pendingImportUri,
                                                            onClearImportUri = { pendingImportUri = null },
                                                            onManageCategoriesClick = {
                                                                currentRootScreen = RootScreen.MANAGE_CATEGORIES
                                                            },
                                                            onExportClick = {
                                                                lifecycleScope.launch {
                                                                    try {
                                                                        val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US).format(java.util.Date())
                                                                        val filename = "KoshaBackup_$timestamp.kosha"
                                                                        val pin = BackupCryptoManager.generatePin()
                                                                        val encryptedData = vm.exportData(pin)
                                                                        
                                                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                                                            val values = android.content.ContentValues().apply {
                                                                                put(android.provider.MediaStore.Downloads.DISPLAY_NAME, filename)
                                                                                put(android.provider.MediaStore.Downloads.MIME_TYPE, "application/octet-stream")
                                                                                put(android.provider.MediaStore.Downloads.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
                                                                            }
                                                                            val uri = contentResolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                                                                            if (uri != null) {
                                                                                contentResolver.openOutputStream(uri)?.use { it.write(encryptedData) }
                                                                                generatedExportPin = pin
                                                                            } else {
                                                                                android.widget.Toast.makeText(this@MainActivity, "Failed to create backup file", android.widget.Toast.LENGTH_LONG).show()
                                                                            }
                                                                        } else {
                                                                            val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                                                                            val file = java.io.File(downloadsDir, filename)
                                                                            file.writeBytes(encryptedData)
                                                                            generatedExportPin = pin
                                                                        }
                                                                    } catch (e: Exception) {
                                                                        android.widget.Toast.makeText(this@MainActivity, "Export Failed: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                                                                    }
                                                                }
                                                            },
                                                            onImportClick = {
                                                                try {
                                                                    isExternalActionActive = true
                                                                    val intent = android.content.Intent(android.content.Intent.ACTION_GET_CONTENT).apply {
                                                                        addCategory(android.content.Intent.CATEGORY_OPENABLE)
                                                                        type = "*/*"
                                                                    }
                                                                    startActivityForResult(android.content.Intent.createChooser(intent, "Select Backup File"), 1001)
                                                                } catch (e: Exception) {
                                                                    isExternalActionActive = false
                                                                    android.widget.Toast.makeText(this@MainActivity, "Picker Error: ${e.message ?: e.javaClass.simpleName}", android.widget.Toast.LENGTH_LONG).show()
                                                                }
                                                            },
                                                            onPerformImport = { uri, pin ->
                                                                lifecycleScope.launch {
                                                                    try {
                                                                        val fileData = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                                                                        if (fileData != null) {
                                                                            vm.importData(fileData, pin)
                                                                            pendingImportUri = null
                                                                        }
                                                                    } catch (e: javax.crypto.AEADBadTagException) {
                                                                        Toast.makeText(this@MainActivity, "Incorrect PIN", Toast.LENGTH_SHORT).show()
                                                                    } catch (e: Exception) {
                                                                        Toast.makeText(this@MainActivity, "Import Failed: ${e.message}", Toast.LENGTH_LONG).show()
                                                                    }
                                                                }
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            RootScreen.ADD_EDIT -> {
                                lockerViewModel?.let { vm ->
                                    AddEditScreen(
                                        viewModel = vm,
                                        initialPassword = prefilledPasswordForAdd,
                                        editingItemId = editingItemId,
                                        onBack = { 
                                            prefilledPasswordForAdd = null
                                            editingItemId = null
                                            currentRootScreen = RootScreen.MAIN_APP 
                                        },
                                        onSave = { 
                                            prefilledPasswordForAdd = null
                                            editingItemId = null
                                            currentRootScreen = RootScreen.MAIN_APP 
                                        }
                                    )
                                }
                            }
                            RootScreen.MANAGE_CATEGORIES -> {
                                lockerViewModel?.let { vm ->
                                    ManageCategoriesScreen(
                                        viewModel = vm,
                                        onBack = { currentRootScreen = RootScreen.MAIN_APP },
                                        onEditCategory = { categoryName ->
                                            selectedCategoryForEdit = categoryName
                                            currentRootScreen = RootScreen.EDIT_CATEGORY
                                        }
                                    )
                                }
                            }
                            RootScreen.EDIT_CATEGORY -> {
                                lockerViewModel?.let { vm ->
                                    selectedCategoryForEdit?.let { categoryName ->
                                        EditCategoryScreen(
                                            viewModel = vm,
                                            categoryName = categoryName,
                                            onBack = { 
                                                selectedCategoryForEdit = null
                                                currentRootScreen = RootScreen.MANAGE_CATEGORIES 
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun wipeAndReset() {
        try {
            val prefs = getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            
            // Delete actual database files
            val dbName = "locker_encrypted.db"
            applicationContext.getDatabasePath(dbName).delete()
            applicationContext.getDatabasePath("$dbName-journal").delete()
            applicationContext.getDatabasePath("$dbName-shm").delete()
            applicationContext.getDatabasePath("$dbName-wal").delete()

            showCorruptedDialog = true
        } catch (e: Exception) {
            android.util.Log.e("Locker", "Failed to wipe database", e)
        }
    }

    private fun showBiometricPrompt() {
        isExternalActionActive = true
        val executor = ContextCompat.getMainExecutor(this)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Kosha")
            .setSubtitle("Authenticate to access your passwords")
            .setAllowedAuthenticators(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    isExternalActionActive = false
                    lastTouchTime = System.currentTimeMillis() // Reset timer on unlock
                    initializeDatabaseAndViewModel()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    isExternalActionActive = false
                    Toast.makeText(this@MainActivity, "Auth Error: $errString", Toast.LENGTH_SHORT).show()
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }

    private fun initializeDatabaseAndViewModel() {
        try {
            val dbPassphrase = getOrGenerateDbPassphrase()
            val db = DatabaseProvider.getDatabase(this, dbPassphrase)
            val repository = LockerRepository(db.lockerDao())
            lockerViewModel = LockerViewModel(repository)
            currentRootScreen = RootScreen.MAIN_APP
        } catch (e: Exception) {
            val msg = "Failed to unlock database: ${e.javaClass.simpleName}: ${e.message}"
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            android.util.Log.e("Locker", msg, e)
        }
    }

    private fun getOrGenerateDbPassphrase(): ByteArray {
        val prefs = getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        val encryptedPassphrase = prefs.getString("db_passphrase", null)
        val ivString = prefs.getString("db_iv", null)

        if (encryptedPassphrase != null && ivString != null) {
            try {
                val cipherText = android.util.Base64.decode(encryptedPassphrase, android.util.Base64.DEFAULT)
                val iv = android.util.Base64.decode(ivString, android.util.Base64.DEFAULT)
                val cipher = if (BYPASS_BIOMETRICS_FOR_TESTING) {
                    cryptoManager.getDecryptCipherForTestingBypass(iv)
                } else {
                    cryptoManager.getDecryptCipher(iv)
                }
                return cipher.doFinal(cipherText)
            } catch (e: Exception) {
                // Stored passphrase was encrypted with a different key
                android.util.Log.w("Locker", "Passphrase decryption failed — key mismatch? Resetting.", e)
                prefs.edit().clear().apply()
                applicationContext.getDatabasePath("locker_encrypted.db").delete()
            }
        }

        return generateAndStoreNewPassphrase(prefs)
    }

    private fun generateAndStoreNewPassphrase(prefs: android.content.SharedPreferences): ByteArray {
        val rawPassphrase = ByteArray(32).apply { SecureRandom().nextBytes(this) }
        val cipher = if (BYPASS_BIOMETRICS_FOR_TESTING) {
            cryptoManager.getEncryptCipherForTestingBypass()
        } else {
            cryptoManager.getEncryptCipher()
        }
        val encrypted = cipher.doFinal(rawPassphrase)
        prefs.edit()
            .putString("db_passphrase", android.util.Base64.encodeToString(encrypted, android.util.Base64.DEFAULT))
            .putString("db_iv", android.util.Base64.encodeToString(cipher.iv, android.util.Base64.DEFAULT))
            .apply()
        return rawPassphrase
    }

    override fun onResume() {
        super.onResume()
        isExternalActionActive = false
        
        val prefs = getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        val timeout = prefs.getLong("auto_lock_timeout", 0L)

        if (currentRootScreen != RootScreen.AUTH && timeout > 0) {
            if (System.currentTimeMillis() - lastTouchTime > timeout) {
                currentRootScreen = RootScreen.AUTH
            } else {
                inactivityJob?.cancel()
                inactivityJob = lifecycleScope.launch {
                    while (true) {
                        kotlinx.coroutines.delay(1000)
                        if (System.currentTimeMillis() - lastTouchTime > timeout) {
                            currentRootScreen = RootScreen.AUTH
                            break
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        inactivityJob?.cancel()
    }

    override fun onStop() {
        super.onStop()
        if (!isExternalActionActive) {
            val prefs = getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
            val timeout = prefs.getLong("auto_lock_timeout", 0L)
            if (timeout == 0L) {
                currentRootScreen = RootScreen.AUTH
            }
        }
    }
}
