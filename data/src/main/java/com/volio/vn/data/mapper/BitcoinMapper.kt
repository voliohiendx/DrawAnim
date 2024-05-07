package com.volio.vn.data.mapper

import com.volio.vn.data.models.BitcoinModel
import com.volio.vn.data.entities.BitcoinPriceEntity

class BitcoinMapper : BaseMapperRepository<BitcoinModel, BitcoinPriceEntity> {
    override fun transform(type: BitcoinModel): BitcoinPriceEntity {
        return BitcoinPriceEntity(
            null,
            type.date,
            null,
            null,
            type.price,
            type.volume
        )
    }

    override fun transformToRepository(type: BitcoinPriceEntity): BitcoinModel {
        return BitcoinModel(
            type.date ?: "",
            type.price ?: 0.0,
            type.volume ?: ""
        )
    }
}