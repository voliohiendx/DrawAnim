package com.volio.vn.data.models.draw

data class DrawPath(
    val timeCreated: Long,
    val listPoint: List<DrawPoint>,
    var size: Float,
    val color: Int,
    var isErase: Boolean,
    var brushType: Int = 0
)
