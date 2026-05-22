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
    val timestamp: Long = System.currentTimeMillis()
)
