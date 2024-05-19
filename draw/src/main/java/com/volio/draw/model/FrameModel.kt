package com.volio.draw.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.UUID

@Parcelize
data class FrameModel(
        val id: String = UUID.randomUUID().toString(),
        var data: MutableList<DataDraw> = mutableListOf(),
        var lastTimeEdit: Long = System.currentTimeMillis()
) : Parcelable

// Moi lan an next sang 1 cai khasc se ex ra 1 anh luu vao bo nho app