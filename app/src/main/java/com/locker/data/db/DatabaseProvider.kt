package com.locker.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.sqlcipher.database.SupportFactory

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE locker_items ADD COLUMN websiteUrl TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE locker_items ADD COLUMN notes TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE locker_items ADD COLUMN customIconData BLOB")
    }
}

object DatabaseProvider {
    @Volatile
    private var INSTANCE: LockerDatabase? = null

    fun getDatabase(context: Context, passphrase: ByteArray): LockerDatabase {
        return INSTANCE ?: synchronized(this) {
            val supportFactory = SupportFactory(passphrase)
            val instance = Room.databaseBuilder(
                context.applicationContext,
                LockerDatabase::class.java,
                "locker_encrypted.db"
            )
            .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Pre-populate default categories and cross-refs in background
                    INSTANCE?.let { database ->
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = database.lockerDao()
                            
                            val defaultCategories = listOf(
                                CategoryEntity("Social", "Icons.Default.People", true, 0),
                                CategoryEntity("Finance", "Icons.Default.AttachMoney", true, 1),
                                CategoryEntity("Work", "Icons.Default.Work", true, 2),
                                CategoryEntity("Personal", "Icons.Default.Person", true, 3),
                                CategoryEntity("Other", "Icons.Default.Folder", true, 4)
                            )
                            defaultCategories.forEach { dao.insertCategory(it) }

                            // Default Brand Mappings
                            val mappings = listOf(
                                "Social" to listOf("brand_facebook", "brand_instagram", "brand_twitter", "brand_linkedin", "brand_snapchat", "brand_discord", "brand_slack", "brand_gmail"),
                                "Finance" to listOf("brand_chase", "brand_bankofamerica", "brand_paypal", "brand_coinbase", "brand_amex", "brand_sbi", "brand_hdfc", "brand_icici", "brand_bankofbaroda", "brand_paytm", "brand_phonepe", "brand_groww", "brand_zerodha"),
                                "Work" to listOf("brand_google", "brand_microsoft", "brand_github", "brand_aws", "brand_icloud", "brand_windows", "brand_apple", "brand_slack"),
                                "Personal" to listOf("brand_icloud", "brand_gmail", "brand_windows", "brand_apple", "brand_amazon", "brand_uber"),
                                "Other" to listOf("brand_netflix", "brand_amazon", "brand_spotify", "brand_uber", "brand_hotstar", "brand_primevideo", "brand_amazonmusic", "brand_flipkart", "brand_myntra")
                            )
                            mappings.forEach { (cat, brands) ->
                                brands.forEach { brandId ->
                                    dao.insertCategoryBrandCrossRef(CategoryBrandCrossRef(cat, brandId))
                                }
                            }
                        }
                    }
                }
            })
            .openHelperFactory(supportFactory)
            .build()
            INSTANCE = instance
            instance
        }
    }
}
