package com.volio.vn.data.entities


import com.google.gson.annotations.SerializedName

data class BitcoinPriceEntity(

    @SerializedName("ChangePercentFromLastMonth")
    val changePercentFromLastMonth: Double?, // -10.6

    @SerializedName("Date")
    val date: String?, // 01/01/2022

    @SerializedName("High")
    val high: Double?, // 47944.9

    @SerializedName("Open")
    val `open`: Double?, // 46217.5

    @SerializedName("Price")
    val price: Double?, // 41321

    @SerializedName("Volume")
    val volume: String? // 517.02K

)