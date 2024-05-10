package com.volio.draw.model.draw

import com.volio.draw.model.BrushType
import com.volio.draw.model.DataDraw
import com.volio.draw.model.DrawPoint
import kotlinx.parcelize.Parcelize

@Parcelize
data class DrawPathModel(
    var time: Long,
    val listPoint: List<DrawPoint>,
    var size: Float,
    val color: Int,
    var brushType: BrushType
): DataDraw
