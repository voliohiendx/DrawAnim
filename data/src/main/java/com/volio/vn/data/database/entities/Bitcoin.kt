package com.volio.vn.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Bitcoin(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "changePercentFromLastMonth")
    val changePercentFromLastMonth: Double?,

    @ColumnInfo(name = "date")
    val date: String?,

    @ColumnInfo(name = "high")
    val high: Double?,

    @ColumnInfo(name = "open")
    val `open`: Double?,

    @ColumnInfo(name = "price")
    val price: Double?,

    @ColumnInfo(name = "volume")
    val volume: String?

)