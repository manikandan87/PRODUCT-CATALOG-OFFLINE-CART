package com.ecommerce.ecom.Room

import android.content.Context

object DatabaseModule {

    fun provideDao(context: Context): DAO {
        val appDatabase = AppDatabase.getDatabase(context)
        return appDatabase.dao()
    }

}