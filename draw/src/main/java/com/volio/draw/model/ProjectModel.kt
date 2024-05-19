package com.volio.draw.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.UUID

@Parcelize
data class ProjectModel(
        var id: String = UUID.randomUUID().toString(),
        var name: String,
        var width: Float,
        var height: Float,
        var background: String,
        val frames: List<FrameModel>
) : Parcelable