//package com.volio.vn.common.di
package com.volio.vn.resource_di.di


import com.volio.vn.data.mapper.BitcoinDBMapper
import com.volio.vn.data.mapper.BitcoinMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MappersModule {

    @Provides
    fun provideBitcoinMapper () : BitcoinMapper = BitcoinMapper()

    @Provides
    fun provideBitcoinDbMapper () : BitcoinDBMapper = BitcoinDBMapper()

}