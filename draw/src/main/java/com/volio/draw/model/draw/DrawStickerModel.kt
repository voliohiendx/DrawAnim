package com.volio.draw.model.draw

import com.volio.draw.model.DataDraw
import com.volio.draw.model.DrawPoint
import kotlinx.parcelize.Parcelize

@Parcelize
data class DrawStickerModel(
    var time: Long,
    val path: String,
    var pointDown: DrawPoint,
    var pointUp: DrawPoint,
) : DataDraw {
    fun copy(): DrawStickerModel {
        return DrawStickerModel(System.currentTimeMillis(), path, pointDown, pointUp)
    }
}