package com.volio.draw.model

import kotlinx.parcelize.Parcelize

@Parcelize
data class DrawPathModel(
    var time: Long,
    val listPoint: List<DrawPoint>,
    var size: Float,
    val color: Int,
    var brushType: Int = 0
):DataDraw
