package com.volio.draw.model

import android.graphics.Bitmap
import com.volio.draw.model.DataDraw
import kotlinx.parcelize.Parcelize

@Parcelize
data class FillDrawData(
    var time: Long,
    var bitmap: Bitmap,
    var x: Int,
    var y: Int,
    var color:Int
) : DataDraw
