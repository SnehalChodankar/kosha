package com.locker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.security.SecureRandom
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(
    snackbarHostState: SnackbarHostState,
    onUsePassword: (String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    
    var length by remember { mutableStateOf(16f) }
    var useUppercase by remember { mutableStateOf(true) }
    var useNumbers by remember { mutableStateOf(true) }
    var useSymbols by remember { mutableStateOf(true) }
    
    var generatedPassword by remember { mutableStateOf("") }
    
    // Auto-generate on first load
    LaunchedEffect(Unit) {
        generatedPassword = generatePassword(length.toInt(), useUppercase, useNumbers, useSymbols)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Password Generator", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Password Display Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = generatedPassword,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    maxLines = 1
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        generatedPassword = generatePassword(length.toInt(), useUppercase, useNumbers, useSymbols)
                    }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Regenerate")
                    Spacer(Modifier.width(8.dp))
                    Text("Regenerate")
                }
                
                FilledTonalButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(generatedPassword))
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Password copied to clipboard")
                        }
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                }

                Button(
                    onClick = {
                        onUsePassword(generatedPassword)
                    }
                ) {
                    Text("Use Password")
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Sliders & Toggles
            Text("Length: ${length.toInt()}", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = length,
                onValueChange = { 
                    length = it 
                    generatedPassword = generatePassword(length.toInt(), useUppercase, useNumbers, useSymbols)
                },
                valueRange = 8f..32f,
                steps = 24
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Uppercase Letters (A-Z)", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = useUppercase, 
                    onCheckedChange = { 
                        useUppercase = it
                        generatedPassword = generatePassword(length.toInt(), useUppercase, useNumbers, useSymbols)
                    }
                )
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Numbers (0-9)", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = useNumbers, 
                    onCheckedChange = { 
                        useNumbers = it
                        generatedPassword = generatePassword(length.toInt(), useUppercase, useNumbers, useSymbols)
                    }
                )
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Symbols (!@#$)", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = useSymbols, 
                    onCheckedChange = { 
                        useSymbols = it
                        generatedPassword = generatePassword(length.toInt(), useUppercase, useNumbers, useSymbols)
                    }
                )
            }
        }
    }
}

private fun generatePassword(length: Int, useUppercase: Boolean, useNumbers: Boolean, useSymbols: Boolean): String {
    val lowercase = "abcdefghijklmnopqrstuvwxyz"
    val uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val numbers = "0123456789"
    val symbols = "!@#\$%^&*()-_=+"
    
    var charPool = lowercase
    if (useUppercase) charPool += uppercase
    if (useNumbers) charPool += numbers
    if (useSymbols) charPool += symbols
    
    val random = SecureRandom()
    return (1..length)
        .map { charPool[random.nextInt(charPool.length)] }
        .joinToString("")
}
