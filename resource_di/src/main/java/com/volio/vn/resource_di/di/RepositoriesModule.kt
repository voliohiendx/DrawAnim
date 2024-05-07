package com.volio.vn.resource_di.di

import com.volio.vn.data.repositories.BitcoinRepository
import com.volio.vn.data.repositories.BitcoinRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Provides
    @Singleton
    fun provideBitcoinRepository(repository: BitcoinRepositoryImpl): BitcoinRepository {
        return repository
    }

}