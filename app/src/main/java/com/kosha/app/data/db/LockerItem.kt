package com.kosha.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey



import androidx.room.ColumnInfo

@Entity(tableName = "locker_items")
data class LockerItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val username: String = "",          // optional username field
    val secretValue: String,
    val category: String,
    val websiteUrl: String = "",        // optional website URL for Autofill
    val notes: String = "",             // optional secure notes
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val customIconData: ByteArray? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LockerItem

        if (id != other.id) return false
        if (title != other.title) return false
        if (username != other.username) return false
        if (secretValue != other.secretValue) return false
        if (category != other.category) return false
        if (websiteUrl != other.websiteUrl) return false
        if (notes != other.notes) return false
        if (customIconData != null) {
            if (other.customIconData == null) return false
            if (!customIconData.contentEquals(other.customIconData)) return false
        } else if (other.customIconData != null) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + secretValue.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + websiteUrl.hashCode()
        result = 31 * result + notes.hashCode()
        result = 31 * result + (customIconData?.contentHashCode() ?: 0)
        result = 31 * result + timestamp.hashCode()
        return result
    }
}
