package com.volio.draw.model.data

import android.graphics.Path
import com.volio.draw.model.BrushType

data class PathDrawData(
    var time: Long,
    val path: Path,
    var size: Float,
    var color: Int,
    var brushType: BrushType
) {
    fun copy(): PathDrawData {
        return PathDrawData(System.currentTimeMillis(), Path(), size, color, brushType)
    }
}
