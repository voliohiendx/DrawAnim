package com.volio.draw.model.draw

import com.volio.draw.model.DataDraw
import kotlinx.parcelize.Parcelize

@Parcelize
data class DrawFillModel(
    var time: Long,
    var x: Int,
    var y: Int,
    var color: Int
) : DataDraw
