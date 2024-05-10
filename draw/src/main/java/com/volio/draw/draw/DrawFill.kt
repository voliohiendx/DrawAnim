package com.volio.draw.draw

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.volio.draw.QueueLinearFloodFiller
import com.volio.draw.model.FillDrawData

class DrawFill(
    private val context: Context,
    var data: FillDrawData,
    private val with: Int,
    private val height: Int
) {
    private var rectScr: Rect = Rect()
    private var rectDst: RectF = RectF()
    private var paintBitmap: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val queueLinearFloodFiller by lazy {
        QueueLinearFloodFiller(context)
    }

    init {
        rectScr.set(0, 0, data.bitmap.width, data.bitmap.height)
        rectDst = RectF(0f, 0f, with.toFloat(), height.toFloat())

        queueLinearFloodFiller.setDataImage(data.bitmap)
    }

    fun onDraw(canvas: Canvas) {
        drawBitmap(canvas)
    }

    private fun drawBitmap(canvas: Canvas) {
        canvas.drawBitmap(data.bitmap, rectScr, rectDst, paintBitmap)
    }

    fun setFloodFill(x: Int, y: Int, newColor: Int, onFloodFillSuccess: (FillDrawData) -> Unit) {
        queueLinearFloodFiller.floodFill(x, y, newColor) {
            data.bitmap = it
            data.x = x
            data.y = y
            onFloodFillSuccess(data)
        }
    }
}
