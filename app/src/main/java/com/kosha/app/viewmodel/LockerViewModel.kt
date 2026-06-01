package com.kosha.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kosha.app.data.db.CategoryEntity
import com.kosha.app.data.db.LockerDao
import com.kosha.app.data.db.LockerItem
import com.kosha.app.domain.backup.BackupCryptoManager
import com.kosha.app.domain.backup.BackupManager
import com.kosha.app.repository.LockerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LockerViewModel(private val repository: LockerRepository) : ViewModel() {
    
    private val _items = MutableStateFlow<List<LockerItem>>(emptyList())
    val items: StateFlow<List<LockerItem>> = _items

    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories

    private val _revealedPasswords = MutableStateFlow<Set<Int>>(emptySet())
    val revealedPasswords: StateFlow<Set<Int>> = _revealedPasswords

    // State for Import Summary
    private val _importSummary = MutableStateFlow<Pair<Int, Int>?>(null)
    val importSummary: StateFlow<Pair<Int, Int>?> = _importSummary

    init {
        viewModelScope.launch {
            repository.getAllItems().collect {
                _items.value = it
            }
        }
        viewModelScope.launch {
            repository.getAllCategories().collect {
                _categories.value = it
            }
        }
    }
    
    fun togglePasswordVisibility(itemId: Int) {
        val current = _revealedPasswords.value.toMutableSet()
        if (current.contains(itemId)) {
            current.remove(itemId)
        } else {
            current.add(itemId)
        }
        _revealedPasswords.value = current
    }

    fun getBrandsForCategory(categoryName: String): Flow<List<String>> {
        return repository.getBrandsForCategory(categoryName)
    }

    fun insert(title: String, username: String, secret: String, categoryName: String, websiteUrl: String = "", notes: String = "", customIconData: ByteArray? = null) {
        viewModelScope.launch {
            repository.insertItem(title, username, secret, categoryName, websiteUrl, notes, customIconData)
        }
    }
    
    fun delete(item: LockerItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun getItemById(id: Int): Flow<LockerItem?> {
        return repository.getItemById(id)
    }

    fun updateItem(item: LockerItem) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }

    fun deleteItems(itemIds: Set<Int>) {
        viewModelScope.launch {
            repository.deleteItems(itemIds)
        }
    }

    fun insertCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.insertCategory(category)
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun toggleBrandForCategory(categoryName: String, brandId: String, isAssigned: Boolean) {
        viewModelScope.launch {
            if (isAssigned) {
                repository.addBrandToCategory(categoryName, brandId)
            } else {
                repository.removeBrandFromCategory(categoryName, brandId)
            }
        }
    }

    // --- Backup & Restore ---
    
    suspend fun exportData(pin: String): ByteArray {
        val payloadJson = BackupManager.createBackupPayload(repository.getDao())
        return BackupCryptoManager.encryptBackup(payloadJson, pin)
    }

    suspend fun importData(fileData: ByteArray, pin: String) {
        val decryptedJson = BackupCryptoManager.decryptBackup(fileData, pin)
        val result = BackupManager.restoreFromPayload(decryptedJson, repository.getDao())
        _importSummary.value = result
    }

    fun clearImportSummary() {
        _importSummary.value = null
    }
}

class LockerViewModelFactory(private val repository: LockerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LockerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LockerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
