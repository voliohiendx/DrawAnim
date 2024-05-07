package com.volio.vn.data.mapper

import com.volio.vn.data.models.BitcoinModel
import com.volio.vn.data.database.entities.Bitcoin

class BitcoinDBMapper : BaseMapperRepository<BitcoinModel, Bitcoin> {
    override fun transform(type: BitcoinModel): Bitcoin {
        return Bitcoin(
            0,
            null,
            type.date,
            null,
            null,
            type.price,
            type.volume
        )
    }

    override fun transformToRepository(type: Bitcoin): BitcoinModel {
        return BitcoinModel(
            type.date ?: "",
            type.price ?: 0.0,
            type.volume ?: ""
        )
    }
}