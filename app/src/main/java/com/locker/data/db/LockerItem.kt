package com.locker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "locker_items")
data class LockerItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val username: String = "",          // optional username field
    val secretValue: String,
    val category: String,
    val websiteUrl: String = "",        // optional website URL for Autofill
    val notes: String = "",             // optional secure notes
    val timestamp: Long = System.currentTimeMillis()
)
