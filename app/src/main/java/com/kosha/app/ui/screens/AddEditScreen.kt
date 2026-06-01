package com.kosha.app.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream
import androidx.compose.material.icons.filled.Image
import com.kosha.app.R
import com.kosha.app.viewmodel.LockerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    viewModel: LockerViewModel,
    initialPassword: String? = null,
    editingItemId: Int? = null,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    
    var title by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf(initialPassword ?: "") }
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }
    var selectedBrand by remember { mutableStateOf<Brand?>(null) }
    var websiteUrl by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var customIconData by remember { mutableStateOf<ByteArray?>(null) }
    var itemLoaded by remember { mutableStateOf(editingItemId == null) }
    
    var context = LocalContext.current
    while (context is android.content.ContextWrapper) {
        if (context is android.app.Activity) break
        context = context.baseContext
    }
    val activity = context as? android.app.Activity

    val launchLogoPicker = {
        com.kosha.app.MainActivity.logoPickerCallback = { uri ->
            if (uri != null) {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val originalBitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()

                    if (originalBitmap != null) {
                        val maxDimension = 96
                        val width = originalBitmap.width
                        val height = originalBitmap.height
                        val ratio = width.toFloat() / height.toFloat()
                        
                        val newWidth = if (width > height) maxDimension else (maxDimension * ratio).toInt()
                        val newHeight = if (height > width) maxDimension else (maxDimension / ratio).toInt()
                        
                        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
                        
                        val outputStream = ByteArrayOutputStream()
                        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        customIconData = outputStream.toByteArray()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_GET_CONTENT).apply {
                addCategory(android.content.Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            activity?.startActivityForResult(android.content.Intent.createChooser(intent, "Select Logo"), 1002)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(editingItemId) {
        if (editingItemId != null) {
            viewModel.getItemById(editingItemId).collect { item ->
                if (item != null) {
                    title = item.title
                    username = item.username
                    password = item.secretValue
                    selectedCategoryName = item.category
                    websiteUrl = item.websiteUrl
                    notes = item.notes
                    customIconData = item.customIconData
                    itemLoaded = true
                }
            }
        }
    }

    // If categories load and nothing is selected, select the first one
    LaunchedEffect(categories) {
        if (selectedCategoryName == null && categories.isNotEmpty()) {
            selectedCategoryName = categories.first().name
        }
    }

    // Fetch brands for the selected category dynamically
    val assignedBrandIds by viewModel.getBrandsForCategory(selectedCategoryName ?: "").collectAsState(initial = emptyList())
    val suggestedBrands = assignedBrandIds.mapNotNull { BrandCatalog.getBrandById(it) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (editingItemId != null) "Edit Password" else "Add Password", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Category Selection (Dynamic Flowing Row) ──────────────────────
            Text(
                "Select Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = cat.name == selectedCategoryName,
                        onClick = {
                            selectedCategoryName = cat.name
                            selectedBrand = null
                        },
                        label = {
                            Text(
                                cat.name,
                                maxLines = 1,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Brand Suggestions (real logos) ────────────────────────────
            Text(
                "Quick Add Brand",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (suggestedBrands.isEmpty()) {
                    Text(
                        "No brands assigned to this category. Edit category in Settings.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    suggestedBrands.forEach { brand ->
                        val isSelected = selectedBrand?.name == brand.name
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                title = brand.name
                                selectedBrand = brand
                                if (websiteUrl.isBlank() && brand.defaultUrl != null) {
                                    websiteUrl = brand.defaultUrl!!
                                }
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected)
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        else
                                            MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = brand.logoRes),
                                    contentDescription = brand.name,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = brand.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Form Fields ───────────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { launchLogoPicker() },
                    contentAlignment = Alignment.Center
                ) {
                    if (customIconData != null) {
                        val bmp = BitmapFactory.decodeByteArray(customIconData, 0, customIconData!!.size)
                        if (bmp != null) {
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Custom Logo",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else if (selectedBrand != null) {
                        Image(
                            painter = painterResource(id = selectedBrand!!.logoRes),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(48.dp)
                        )
                    } else if (title.isNotBlank()) {
                        Text(
                            text = title.first().uppercase(),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        )
                    } else {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tap logo to upload custom image", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title  (e.g. Netflix)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username / Email  (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password or PIN") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = websiteUrl,
                onValueChange = { websiteUrl = it },
                label = { Text("Website URL (for Autofill)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Secure Notes  (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    if (title.isNotBlank() && password.isNotBlank() && selectedCategoryName != null) {
                        if (editingItemId != null) {
                            viewModel.updateItem(
                                com.kosha.app.data.db.LockerItem(
                                    id = editingItemId,
                                    title = title,
                                    username = username.trim(),
                                    secretValue = password,
                                    category = selectedCategoryName!!,
                                    websiteUrl = websiteUrl.trim(),
                                    notes = notes.trim(),
                                    customIconData = customIconData
                                )
                            )
                        } else {
                            viewModel.insert(
                                title = title, 
                                username = username.trim(), 
                                secret = password, 
                                categoryName = selectedCategoryName!!,
                                websiteUrl = websiteUrl.trim(),
                                notes = notes.trim(),
                                customIconData = customIconData
                            )
                        }
                        onSave()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = title.isNotBlank() && password.isNotBlank() && selectedCategoryName != null,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save to Vault", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
