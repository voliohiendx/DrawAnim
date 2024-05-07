package com.volio.draw.draw

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import com.bumptech.glide.Glide
import com.volio.draw.model.DrawPoint
import com.volio.draw.model.DrawStickerModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrawSticker(
    private val context: Context,
    var data: DrawStickerModel
) {
    private var paintBitmap: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bitmap: Bitmap? = null
    private var rectScr: Rect = Rect()
    private var rectDst: RectF = RectF()
    var pointOrigin = Point(0, 0)

    init {
        rectDst.set(
            data.pointDown.x,
            data.pointDown.y,
            data.pointUp.x - data.pointDown.x,
            data.pointUp.y - data.pointDown.y
        )
        loadImage()
    }

    private fun loadImage() {
        CoroutineScope(Dispatchers.IO).launch {
            bitmap = Glide.with(context).asBitmap()
                .load(data.path)
                .submit().get()

            bitmap?.let {
                rectScr.set(0, 0, it.width, it.height)
            }
        }
    }

    fun onDraw(canvas: Canvas) {
        bitmap?.let {
            canvas.drawBitmap(it, rectScr, rectDst, paintBitmap)
        }
    }

    fun onTouch(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pointOrigin = Point(event.x.toInt(), event.y.toInt())
            }

            MotionEvent.ACTION_MOVE -> {
                rectDst.set(
                    pointOrigin.x.toFloat(),
                    pointOrigin.y.toFloat(),
                    event.x - pointOrigin.x,
                    event.y - pointOrigin.y
                )
            }

            MotionEvent.ACTION_UP -> {
                data.pointUp = (DrawPoint(event.x, event.y))
                data.pointDown = (DrawPoint(pointOrigin.x.toFloat(), pointOrigin.y.toFloat()))
            }
        }
    }


}
