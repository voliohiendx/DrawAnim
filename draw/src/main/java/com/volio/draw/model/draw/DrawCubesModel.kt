package com.volio.draw.model.draw

import com.volio.draw.model.BrushType
import com.volio.draw.model.DataDraw
import com.volio.draw.model.DrawPoint
import com.volio.draw.model.TypeCubes
import kotlinx.parcelize.Parcelize

@Parcelize
data class DrawCubesModel(
    var time: Long,
    var pointDown: DrawPoint,
    var pointUp: DrawPoint,
    var size: Float,
    val color: Int,
    var brushType: BrushType,
    var typeCubes: TypeCubes
): DataDraw
