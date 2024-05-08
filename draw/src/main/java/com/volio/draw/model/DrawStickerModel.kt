package com.volio.draw.model

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