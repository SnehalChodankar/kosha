package com.locker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val name: String,
    val iconResName: String, // e.g. "Icons.Default.Folder" or custom identifiers
    val isDefault: Boolean = false,
    val orderIndex: Int = 0
)
