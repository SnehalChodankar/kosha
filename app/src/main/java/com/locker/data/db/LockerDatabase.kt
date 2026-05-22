package com.locker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LockerItem::class, CategoryEntity::class, CategoryBrandCrossRef::class],
    version = 3,
    exportSchema = false
)
abstract class LockerDatabase : RoomDatabase() {
    abstract fun lockerDao(): LockerDao
}
