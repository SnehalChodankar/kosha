package com.locker.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.asImageBitmap
import com.locker.data.db.LockerItem
import com.locker.viewmodel.LockerViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.FilterChip
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import com.locker.ui.screens.BrandCatalog

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    viewModel: LockerViewModel,
    snackbarHostState: SnackbarHostState,
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit
) {
    val items by viewModel.items.collectAsState()
    val revealedPasswords by viewModel.revealedPasswords.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }
    var selectedItemIds by remember { mutableStateOf(setOf<Int>()) }
    val focusManager = LocalFocusManager.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    val filteredItems = items.filter { item ->
        val matchesSearch = if (searchQuery.isBlank()) true else {
            item.title.lowercase().contains(searchQuery.lowercase()) || 
            item.username.lowercase().contains(searchQuery.lowercase()) ||
            item.category.lowercase().contains(searchQuery.lowercase())
        }
        val matchesCategory = if (selectedCategoryFilter == null) true else {
            item.category == selectedCategoryFilter
        }
        matchesSearch && matchesCategory
    }

    val groupedItems = filteredItems.groupBy { it.category }

    var itemToDelete by remember { mutableStateOf<LockerItem?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showMultiDeleteConfirmDialog by remember { mutableStateOf(false) }

    if (showDeleteConfirmDialog && itemToDelete != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete '${itemToDelete?.title}'? This action cannot be undone.") },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        itemToDelete?.let { viewModel.delete(it) }
                        showDeleteConfirmDialog = false
                        itemToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showMultiDeleteConfirmDialog && selectedItemIds.isNotEmpty()) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showMultiDeleteConfirmDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete ${selectedItemIds.size} items? This action cannot be undone.") },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        viewModel.deleteItems(selectedItemIds)
                        selectedItemIds = emptySet()
                        showMultiDeleteConfirmDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showMultiDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            if (selectedItemIds.isNotEmpty()) {
                TopAppBar(
                    title = { Text("${selectedItemIds.size} Selected", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { selectedItemIds = emptySet() }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Selection")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            showMultiDeleteConfirmDialog = true
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Selected", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("Kosha", fontWeight = FontWeight.Bold) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Password")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
        ) {
            item { 
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    placeholder = { Text("Search title, username...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
            }

            if (categories.isNotEmpty()) {
                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategoryFilter == null,
                                onClick = { selectedCategoryFilter = null },
                                label = { Text("All") }
                            )
                        }
                        items(categories) { category ->
                            FilterChip(
                                selected = selectedCategoryFilter == category.name,
                                onClick = { 
                                    selectedCategoryFilter = if (selectedCategoryFilter == category.name) null else category.name 
                                },
                                label = { Text(category.name) }
                            )
                        }
                    }
                }
            }

            groupedItems.forEach { (categoryName, categoryItems) ->
                item {
                    val catObj = categories.find { it.name == categoryName }
                    val iconVector = catObj?.iconResName?.let { getCategoryIcon(it) } ?: Icons.Default.Folder
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Icon(iconVector, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = categoryName.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                items(categoryItems) { item ->
                    val isSelected = selectedItemIds.contains(item.id)
                    val isSelectionMode = selectedItemIds.isNotEmpty()
                    LockerItemCard(
                        item = item,
                        isRevealed = revealedPasswords.contains(item.id),
                        isSelected = isSelected,
                        isSelectionMode = isSelectionMode,
                        onDeleteClick = { 
                            itemToDelete = item
                            showDeleteConfirmDialog = true
                        },
                        onEditClick = { onEditClick(item.id) },
                        onLongClick = {
                            selectedItemIds = if (isSelected) selectedItemIds - item.id else selectedItemIds + item.id
                        },
                        onClick = {
                            if (selectedItemIds.isNotEmpty()) {
                                selectedItemIds = if (isSelected) selectedItemIds - item.id else selectedItemIds + item.id
                            }
                        },
                        onRevealClick = { viewModel.togglePasswordVisibility(item.id) },
                        snackbarHostState = snackbarHostState
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            if (items.isEmpty()) {
                item {
                    Text(
                        "No passwords saved yet. Click the + button to add one.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LockerItemCard(
    item: LockerItem, 
    isRevealed: Boolean,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    onRevealClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    
    // Try to find a matching brand logo based on the title
    val brand = BrandCatalog.findBrandByTitle(item.title)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = { 
                    onClick()
                    if (!isSelectionMode) expanded = !expanded 
                },
                onLongClick = onLongClick
            )
            .then(
                if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                else Modifier
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Brand logo or initial avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(brand?.color?.copy(alpha = 0.12f) ?: MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.customIconData != null) {
                        val bmp = android.graphics.BitmapFactory.decodeByteArray(item.customIconData, 0, item.customIconData.size)
                        if (bmp != null) {
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = item.title,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = item.title.firstOrNull()?.uppercase() ?: "?",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    } else if (brand != null) {
                        Image(
                            painter = painterResource(id = brand.logoRes),
                            contentDescription = brand.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(28.dp)
                        )
                    } else {
                        Text(
                            text = item.title.firstOrNull()?.uppercase() ?: "?",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                // Title + username
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    if (item.username.isNotBlank()) {
                        Text(
                            text = item.username,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    }
                    if (isRevealed && !expanded) {
                        Text(
                            text = item.secretValue,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Action icons (always visible)
                Row {
                    IconButton(
                        onClick = onRevealClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Reveal Password",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(item.secretValue))
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Password copied to clipboard")
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Password",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            // Expanded Content (Revealed Password + Extra Actions)
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Password",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isRevealed) item.secretValue else "••••••••",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = if (isRevealed) 1.sp else 4.sp
                        )
                    }
                    
                    Row {
                        IconButton(onClick = onEditClick) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(onClick = onDeleteClick) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                if (item.websiteUrl.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Website: ${item.websiteUrl}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (item.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Notes: ${item.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

fun getCategoryIcon(iconResName: String): ImageVector {
    return when (iconResName) {
        "Icons.Default.Star" -> Icons.Default.Star
        "Icons.Default.ShoppingCart" -> Icons.Default.ShoppingCart
        "Icons.Default.Work" -> Icons.Default.Work
        "Icons.Default.Home" -> Icons.Default.Home
        "Icons.Default.Person" -> Icons.Default.Person
        "Icons.Default.Favorite" -> Icons.Default.Favorite
        "Icons.Default.Build" -> Icons.Default.Build
        "Icons.Default.Camera" -> Icons.Default.Camera
        "Icons.Default.DirectionsCar" -> Icons.Default.DirectionsCar
        "Icons.Default.Face" -> Icons.Default.Face
        "Icons.Default.Flight" -> Icons.Default.Flight
        "Icons.Default.LocalDining" -> Icons.Default.LocalDining
        "Icons.Default.MenuBook" -> Icons.Default.MenuBook
        "Icons.Default.Pets" -> Icons.Default.Pets
        "Icons.Default.School" -> Icons.Default.School
        "Icons.Default.SportsEsports" -> Icons.Default.SportsEsports
        "Icons.Default.People" -> Icons.Default.Person
        "Icons.Default.AttachMoney" -> Icons.Default.ShoppingCart
        else -> Icons.Default.Folder
    }
}
