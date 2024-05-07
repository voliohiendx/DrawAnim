package com.volio.draw.model

import android.graphics.Path

data class PathDrawData(
    var time: Long,
    val path: Path,
    var size: Float,
    var color: Int,
    var brushType: Int
)
