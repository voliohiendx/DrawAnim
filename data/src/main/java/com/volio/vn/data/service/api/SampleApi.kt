package com.volio.vn.data.service.api

import com.volio.vn.data.entities.BitcoinPriceEntity
import retrofit2.http.GET

interface SampleApi {
    @GET("/bitcoin/historical_prices")
    suspend fun getHistoricalPrices(): List<BitcoinPriceEntity>
}
