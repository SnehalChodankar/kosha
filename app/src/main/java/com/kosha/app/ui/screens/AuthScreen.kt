package com.kosha.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.kosha.app.R

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.width

@Composable
fun AuthScreen(
    onAuthenticateClick: () -> Unit,
    onDuressTriggered: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDuressInput by remember { mutableStateOf(false) }
    var duressPin by remember { mutableStateOf("") }
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("secure_prefs", android.content.Context.MODE_PRIVATE) }
    val storedHash = prefs.getString("duress_pin_hash", null)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.kosha_with_name),
            contentDescription = "Kosha Logo",
            modifier = Modifier
                .size(160.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            if (storedHash != null) {
                                duressPin = ""
                                showDuressInput = true
                            }
                        }
                    )
                }
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        if (showDuressInput) {
            TextField(
                value = duressPin,
                onValueChange = { duressPin = it },
                modifier = Modifier.width(160.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xFF2196F3), // Blue bottom border
                    unfocusedIndicatorColor = Color(0xFF2196F3).copy(alpha = 0.5f),
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        Text(
            text = "Kosha is Locked",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Authenticate to access your secure data.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {
            if (showDuressInput && duressPin.isNotEmpty()) {
                if (storedHash != null) {
                    val md = java.security.MessageDigest.getInstance("SHA-256")
                    val hashBytes = md.digest(duressPin.toByteArray(Charsets.UTF_8))
                    val hashStr = android.util.Base64.encodeToString(hashBytes, android.util.Base64.NO_WRAP)
                    if (hashStr == storedHash) {
                        onDuressTriggered()
                    } else {
                        onAuthenticateClick()
                    }
                } else {
                    onAuthenticateClick()
                }
            } else {
                onAuthenticateClick()
            }
        }) {
            Text("Unlock")
        }
    }
}
