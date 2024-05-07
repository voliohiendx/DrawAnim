package com.volio.vn.resource_di.di

import com.volio.vn.data.repositories.BitcoinRepository
import com.volio.vn.data.usecases.BitcoinUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCasesModule {

    @Provides
    fun provideBitcoinUseCase(
        repo: BitcoinRepository
    ): BitcoinUseCase = BitcoinUseCase(repo)

}