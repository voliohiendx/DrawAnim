package com.volio.vn.data.models.draw

import android.graphics.Path

data class DataPathDraw(
    var time: Long,
    val path: Path,
    var size: Float,
    var color: Int,
    var isErase: Boolean,
    var brushType: Int
)
