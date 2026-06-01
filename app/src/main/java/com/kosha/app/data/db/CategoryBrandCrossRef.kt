package com.kosha.app.data.db

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "category_brand_cross_ref",
    primaryKeys = ["categoryName", "brandId"],
    indices = [Index(value = ["brandId"])]
)
data class CategoryBrandCrossRef(
    val categoryName: String,
    val brandId: String
)
