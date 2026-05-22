package com.locker.domain.backup

import com.locker.data.db.CategoryBrandCrossRef
import com.locker.data.db.CategoryEntity
import com.locker.data.db.LockerItem

data class BackupPayload(
    val version: Int = 1,
    val items: List<LockerItem>,
    val categories: List<CategoryEntity>,
    val categoryBrands: List<CategoryBrandCrossRef>
)
