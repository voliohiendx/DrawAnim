package com.volio.vn.data.usecases

import com.volio.vn.data.models.BitcoinModel
import com.volio.vn.data.repositories.BitcoinRepository
import javax.inject.Inject

class BitcoinUseCase @Inject constructor(
    private val repo: BitcoinRepository
) : BaseUseCase() {

    suspend fun getBitcoinPrices(): List<BitcoinModel> {
        return runWithCheckException {
            if (repo.getBitcoinHistoryRemote().isEmpty()) {
                repo.getBitcoinHistoryInLocal()
            } else {
                repo.getBitcoinHistoryRemote()
            }
        } ?: emptyList()
    }

    suspend fun getBitcoinPricesByResult(): Result<List<BitcoinModel>> {
        return runWithCheckExceptionByResult {
            if (repo.getBitcoinHistoryRemote().isEmpty()) {
                repo.getBitcoinHistoryInLocal()
            } else {
                repo.getBitcoinHistoryRemote()
            }
        }
    }
}


