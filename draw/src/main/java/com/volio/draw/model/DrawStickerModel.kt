package com.volio.draw.model

data class DrawStickerModel(
    var time: Long,
    val path: String,
    var pointDown: DrawPoint,
    var pointUp: DrawPoint,
):DataDraw