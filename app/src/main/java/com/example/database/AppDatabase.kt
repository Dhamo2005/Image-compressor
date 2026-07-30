package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.model.CompressionJob
import com.example.model.CompressionProfile
import com.example.model.DocumentRule
import com.example.model.LogItem

@Database(
    entities = [
        DocumentRule::class,
        CompressionProfile::class,
        LogItem::class,
        CompressionJob::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentRuleDao(): DocumentRuleDao
    abstract fun profileDao(): ProfileDao
    abstract fun logDao(): LogDao
    abstract fun compressionJobDao(): CompressionJobDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "image_compressor_pro.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
