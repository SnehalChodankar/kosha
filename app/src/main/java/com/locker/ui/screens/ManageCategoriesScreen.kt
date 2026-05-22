package com.locker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import com.locker.data.db.CategoryEntity
import com.locker.viewmodel.LockerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesScreen(
    viewModel: LockerViewModel,
    onBack: () -> Unit,
    onEditCategory: (String) -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    
    val availableIcons = listOf(
        "Icons.Default.Folder" to Icons.Default.Folder,
        "Icons.Default.Star" to Icons.Default.Star,
        "Icons.Default.ShoppingCart" to Icons.Default.ShoppingCart,
        "Icons.Default.Work" to Icons.Default.Work,
        "Icons.Default.Home" to Icons.Default.Home,
        "Icons.Default.Person" to Icons.Default.Person,
        "Icons.Default.People" to Icons.Default.Person, // Fallback for old default "Social"
        "Icons.Default.AttachMoney" to Icons.Default.ShoppingCart, // Fallback for old default "Finance"
        "Icons.Default.Favorite" to Icons.Default.Favorite,
        "Icons.Default.Build" to Icons.Default.Build,
        "Icons.Default.Camera" to Icons.Default.Camera,
        "Icons.Default.DirectionsCar" to Icons.Default.DirectionsCar,
        "Icons.Default.Face" to Icons.Default.Face,
        "Icons.Default.Flight" to Icons.Default.Flight,
        "Icons.Default.LocalDining" to Icons.Default.LocalDining,
        "Icons.Default.MenuBook" to Icons.Default.MenuBook,
        "Icons.Default.Pets" to Icons.Default.Pets,
        "Icons.Default.School" to Icons.Default.School,
        "Icons.Default.SportsEsports" to Icons.Default.SportsEsports
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Categories", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Category")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(categories) { category ->
                val assignedBrands by viewModel.getBrandsForCategory(category.name).collectAsState(initial = emptyList())
                val count = assignedBrands.size
                
                Column {
                    ListItem(
                        headlineContent = { Text(category.name, fontWeight = FontWeight.Bold) },
                        supportingContent = {
                            val typeText = if (category.isDefault) "Default Category" else "Custom Category"
                            Text("$typeText ($count brands)")
                        },
                        leadingContent = {
                            val iconVector = availableIcons.find { it.first == category.iconResName }?.second ?: Icons.Default.Folder
                            IconButton(onClick = { editingCategory = category }) {
                                Icon(iconVector, contentDescription = "Change Icon", tint = MaterialTheme.colorScheme.primary)
                            }
                        },
                        trailingContent = {
                            Row {
                            IconButton(onClick = { onEditCategory(category.name) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Brands", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (!category.isDefault) {
                                IconButton(onClick = { viewModel.deleteCategory(category) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    },
                    modifier = Modifier.clickable { onEditCategory(category.name) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }

        if (showAddDialog || editingCategory != null) {
            val isEditing = editingCategory != null
            var categoryName by remember { mutableStateOf(editingCategory?.name ?: "") }
            var selectedIconName by remember { mutableStateOf(editingCategory?.iconResName ?: availableIcons[0].first) }

            AlertDialog(
                onDismissRequest = { 
                    showAddDialog = false
                    editingCategory = null 
                },
                title = { Text(if (isEditing) "Edit Category" else "New Category") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = categoryName,
                            onValueChange = { categoryName = it },
                            label = { Text("Category Name") },
                            singleLine = true,
                            enabled = !isEditing
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Select Icon", style = MaterialTheme.typography.labelMedium)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            items(availableIcons) { (name, vector) ->
                                val isSelected = name == selectedIconName
                                IconButton(
                                    onClick = { selectedIconName = name },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                                            else Color.Transparent,
                                            shape = CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = vector,
                                        contentDescription = name,
                                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                                               else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (categoryName.isNotBlank()) {
                                if (isEditing) {
                                    viewModel.insertCategory(editingCategory!!.copy(iconResName = selectedIconName))
                                } else {
                                    viewModel.insertCategory(
                                        CategoryEntity(
                                            name = categoryName.trim(),
                                            iconResName = selectedIconName,
                                            isDefault = false
                                        )
                                    )
                                }
                                showAddDialog = false
                                editingCategory = null
                            }
                        },
                        enabled = categoryName.isNotBlank()
                    ) {
                        Text(if (isEditing) "Save" else "Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showAddDialog = false
                        editingCategory = null 
                    }) { Text("Cancel") }
                }
            )
        }
    }
}
