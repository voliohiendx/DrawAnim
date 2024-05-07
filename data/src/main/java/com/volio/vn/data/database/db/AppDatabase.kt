package com.volio.vn.data.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.volio.vn.data.database.dao.BitcoinDao
import com.volio.vn.data.database.entities.Bitcoin

@Database(entities = [Bitcoin::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bitcoinDao(): BitcoinDao
}