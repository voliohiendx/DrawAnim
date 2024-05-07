package com.volio.vn.resource_di.di

import android.app.Application
import androidx.room.Room
import com.volio.vn.data.database.dao.BitcoinDao
import com.volio.vn.data.database.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideRoom(appContext: Application): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "database-name"
        ).build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): BitcoinDao {
        return db.bitcoinDao()
    }

}