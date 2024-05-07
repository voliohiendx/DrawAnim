package com.volio.vn.data.repositories

import com.volio.vn.data.database.db.AppDatabase
import com.volio.vn.data.mapper.BitcoinDBMapper
import com.volio.vn.data.mapper.BitcoinMapper
import com.volio.vn.data.models.BitcoinModel
import com.volio.vn.data.service.api.SampleApi
import javax.inject.Inject

class BitcoinRepositoryImpl @Inject constructor(
    private val apiRemote: SampleApi,
    private val appDb: AppDatabase,
    private val bitcoinMapper: BitcoinMapper,
    private val bitcoinDbMapper: BitcoinDBMapper,
) : BitcoinRepository {

    override suspend fun getBitcoinHistoryInLocal(): List<BitcoinModel> {
        val listBitcoins = appDb.bitcoinDao().getAll()

        // Save to data base if fetch remote success
        if (listBitcoins.isNotEmpty()) {
            appDb.bitcoinDao().insertAll(*listBitcoins.toTypedArray())
        }

        return listBitcoins.map { bitcoinDbMapper.transformToRepository(it) }
    }

    override suspend fun getBitcoinHistoryRemote(): List<BitcoinModel> {
        val historicalPriceEntities = apiRemote.getHistoricalPrices()
        return historicalPriceEntities.map { bitcoinMapper.transformToRepository(it) }
    }

}