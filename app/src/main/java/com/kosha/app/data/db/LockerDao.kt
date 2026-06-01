package com.kosha.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LockerDao {
    // --- LockerItem ---
    @Query("SELECT * FROM locker_items ORDER BY timestamp DESC")
    fun getAllItems(): Flow<List<LockerItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: LockerItem)

    @Query("SELECT * FROM locker_items WHERE id = :id LIMIT 1")
    fun getItemById(id: Int): Flow<LockerItem?>

    @Update
    suspend fun updateItem(item: LockerItem)

    @Delete
    suspend fun deleteItem(item: LockerItem)

    @Query("DELETE FROM locker_items WHERE id IN (:itemIds)")
    suspend fun deleteItems(itemIds: Set<Int>)

    // --- CategoryEntity ---
    @Query("SELECT * FROM categories ORDER BY orderIndex ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    // --- CategoryBrandCrossRef ---
    @Query("SELECT brandId FROM category_brand_cross_ref WHERE categoryName = :categoryName")
    fun getBrandsForCategory(categoryName: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategoryBrandCrossRef(crossRef: CategoryBrandCrossRef)

    @Delete
    suspend fun deleteCategoryBrandCrossRef(crossRef: CategoryBrandCrossRef)
    
    @Query("DELETE FROM category_brand_cross_ref WHERE categoryName = :categoryName AND brandId = :brandId")
    suspend fun removeBrandFromCategory(categoryName: String, brandId: String)
}
