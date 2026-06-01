package com.kosha.app.repository

import com.kosha.app.data.db.CategoryBrandCrossRef
import com.kosha.app.data.db.CategoryEntity
import com.kosha.app.data.db.LockerDao
import com.kosha.app.data.db.LockerItem
import kotlinx.coroutines.flow.Flow

class LockerRepository(private val lockerDao: LockerDao) {
    fun getDao(): LockerDao = lockerDao

    fun getAllItems(): Flow<List<LockerItem>> = lockerDao.getAllItems()

    suspend fun insertItem(title: String, username: String, secretValue: String, categoryName: String, websiteUrl: String = "", notes: String = "", customIconData: ByteArray? = null) {
        lockerDao.insertItem(
            LockerItem(
                title = title,
                username = username,
                secretValue = secretValue,
                category = categoryName,
                websiteUrl = websiteUrl,
                notes = notes,
                customIconData = customIconData
            )
        )
    }

    suspend fun deleteItem(item: LockerItem) {
        lockerDao.deleteItem(item)
    }

    fun getItemById(id: Int): Flow<LockerItem?> = lockerDao.getItemById(id)

    suspend fun updateItem(item: LockerItem) {
        lockerDao.updateItem(item)
    }

    suspend fun deleteItems(itemIds: Set<Int>) {
        lockerDao.deleteItems(itemIds)
    }

    // --- Categories ---
    fun getAllCategories(): Flow<List<CategoryEntity>> = lockerDao.getAllCategories()
    
    suspend fun insertCategory(category: CategoryEntity) {
        lockerDao.insertCategory(category)
    }
    
    suspend fun deleteCategory(category: CategoryEntity) {
        lockerDao.deleteCategory(category)
    }
    
    // --- Brands ---
    fun getBrandsForCategory(categoryName: String): Flow<List<String>> = lockerDao.getBrandsForCategory(categoryName)
    
    suspend fun addBrandToCategory(categoryName: String, brandId: String) {
        lockerDao.insertCategoryBrandCrossRef(CategoryBrandCrossRef(categoryName, brandId))
    }
    
    suspend fun removeBrandFromCategory(categoryName: String, brandId: String) {
        lockerDao.removeBrandFromCategory(categoryName, brandId)
    }
}
