package com.kosha.app.domain.backup

import com.kosha.app.data.db.CategoryBrandCrossRef
import com.kosha.app.data.db.CategoryEntity
import com.kosha.app.data.db.LockerItem

data class BackupPayload(
    val version: Int = 1,
    val items: List<LockerItem>,
    val categories: List<CategoryEntity>,
    val categoryBrands: List<CategoryBrandCrossRef>
)
