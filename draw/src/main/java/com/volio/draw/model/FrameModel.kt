package com.volio.draw.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FrameModel(
    val data: List<DataDraw>
):Parcelable
