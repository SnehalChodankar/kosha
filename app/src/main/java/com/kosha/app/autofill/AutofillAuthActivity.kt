package com.kosha.app.autofill

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.autofill.Dataset
import android.service.autofill.FillResponse
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import androidx.fragment.app.FragmentActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.kosha.app.R
import com.kosha.app.data.db.CryptoManager
import com.kosha.app.data.db.DatabaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AutofillAuthActivity : FragmentActivity() {

    private val cryptoManager = CryptoManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // This is a transparent activity, so we immediately show BiometricPrompt
        showBiometricPrompt()
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    finishWithCancel()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    handleSuccessfulUnlock()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    finishWithCancel()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Kosha")
            .setSubtitle("Authenticate to autofill passwords")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun handleSuccessfulUnlock() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dbPassphrase = getDbPassphrase()
                if (dbPassphrase == null) {
                    withContext(Dispatchers.Main) { finishWithCancel() }
                    return@launch
                }

                val db = DatabaseProvider.getDatabase(this@AutofillAuthActivity, dbPassphrase)
                val items = db.lockerDao().getAllItems().first()

                val packageName = this@AutofillAuthActivity.intent.getStringExtra("package_name") ?: ""
                val webDomain = this@AutofillAuthActivity.intent.getStringExtra("web_domain") ?: ""
                val usernameIds = this@AutofillAuthActivity.intent.getParcelableArrayListExtra<AutofillId>("username_ids") ?: emptyList()
                val passwordIds = this@AutofillAuthActivity.intent.getParcelableArrayListExtra<AutofillId>("password_ids") ?: emptyList()

                // Filter items that match the domain or package name
                val matchingItems = items.filter { item ->
                    val url = item.websiteUrl.lowercase()
                    val title = item.title.lowercase()
                    val pkg = packageName.lowercase()
                    val dom = webDomain.lowercase()

                    (url.isNotBlank() && (pkg.contains(url) || dom.contains(url))) ||
                    (dom.isNotBlank() && url.contains(dom)) ||
                    (pkg.contains(title))
                }

                if (matchingItems.isEmpty()) {
                    // No passwords found for this app/website
                    withContext(Dispatchers.Main) { finishWithCancel() }
                    return@launch
                }

                // Build the FillResponse with real data
                val responseBuilder = FillResponse.Builder()

                matchingItems.forEach { item ->
                    val datasetBuilder = Dataset.Builder()
                    
                    val presentation = RemoteViews(packageName, android.R.layout.simple_list_item_1).apply {
                        setTextViewText(android.R.id.text1, item.username.ifBlank { item.title })
                    }

                    usernameIds.forEach { id ->
                        if (item.username.isNotBlank()) {
                            datasetBuilder.setValue(id, AutofillValue.forText(item.username), presentation)
                        }
                    }
                    passwordIds.forEach { id ->
                        datasetBuilder.setValue(id, AutofillValue.forText(item.secretValue), presentation)
                    }
                    responseBuilder.addDataset(datasetBuilder.build())
                }

                val replyIntent = Intent().apply {
                    putExtra(android.view.autofill.AutofillManager.EXTRA_AUTHENTICATION_RESULT, responseBuilder.build())
                }

                withContext(Dispatchers.Main) {
                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { finishWithCancel() }
            }
        }
    }

    private fun getDbPassphrase(): ByteArray? {
        val prefs = getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        val encryptedPassphrase = prefs.getString("db_passphrase", null)
        val ivString = prefs.getString("db_iv", null) ?: return null

        if (encryptedPassphrase != null) {
            val cipherText = android.util.Base64.decode(encryptedPassphrase, android.util.Base64.DEFAULT)
            val iv = android.util.Base64.decode(ivString, android.util.Base64.DEFAULT)
            val cipher = cryptoManager.getDecryptCipher(iv)
            return cipher.doFinal(cipherText)
        }
        return null
    }

    private fun finishWithCancel() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
