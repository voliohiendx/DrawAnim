package com.volio.draw.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProjectModel(
    val frames: List<FrameModel>
) : Parcelable