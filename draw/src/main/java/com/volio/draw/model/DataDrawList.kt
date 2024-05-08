package com.volio.draw.model

import android.os.Parcelable
import com.volio.draw.model.DataDraw
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataDrawList(
    val data: List<DataDraw>
):Parcelable
