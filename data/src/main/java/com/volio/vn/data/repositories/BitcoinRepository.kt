package com.volio.vn.data.repositories

import com.volio.vn.data.models.BitcoinModel

interface BitcoinRepository {

    suspend fun getBitcoinHistoryInLocal(): List<BitcoinModel>

    suspend fun getBitcoinHistoryRemote(): List<BitcoinModel>
}