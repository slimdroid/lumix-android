package com.slimdroid.lumix.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.slimdroid.lumix.core.database.dao.DeviceDao
import com.slimdroid.lumix.core.database.entity.DeviceEntity

@Database(
    entities = [
        DeviceEntity::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao

    companion object {
        private const val DB_NAME = "lumix.db"
        private var db: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return db ?: Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                .build()
                .also { db = it }
        }
    }
}