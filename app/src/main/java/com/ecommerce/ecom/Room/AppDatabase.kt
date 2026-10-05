package com.ecommerce.ecom.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ecommerce.ecom.Room.Entity.CartItem

@Database(
    entities = [CartItem::class],
    version = 1,
    exportSchema = false
)


abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ecommerce_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}