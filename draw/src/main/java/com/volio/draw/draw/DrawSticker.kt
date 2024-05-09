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
import kotlinx.coroutines.withContext

class DrawSticker(
    private val context: Context, var data: DrawStickerModel, private val updateView: () -> Unit
) {
    private var paintBitmap: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bitmap: Bitmap? = null
    private var rectScr: Rect = Rect()
    private var rectDst: RectF = RectF()
    var pointOrigin = Point(0, 0)
    var pointUp = DrawPoint(0f, 0f)
    var pointDown = DrawPoint(0f, 0f)

    init {

        rectDst.set(
            data.pointDown.x, data.pointDown.y, data.pointUp.x, data.pointUp.y
        )

        CoroutineScope(Dispatchers.IO).launch {
            loadImage()
            withContext(Dispatchers.Main) {
                updateView.invoke()
            }
        }
    }


    private fun loadImage() {
        CoroutineScope(Dispatchers.IO).launch {
            bitmap = Glide.with(context).asBitmap().load(data.path).submit().get()

            bitmap?.let {
                rectScr.set(0, 0, it.width, it.height)
            }
        }
    }

    fun onDraw(canvas: Canvas) {
        bitmap?.let {
            Log.d("HIUIUIUIUIU", "onDraw: ")
            canvas.drawBitmap(it, rectScr, rectDst, paintBitmap)
        }
    }

    fun onActionDown(x: Float, y: Float) {
        pointOrigin = Point(x.toInt(), y.toInt())
    }

    fun onActionMove(x: Float, y: Float) {
        if (y > pointOrigin.y) {
            if (x > pointOrigin.x) {
                pointDown = DrawPoint(
                    pointOrigin.x.toFloat(), pointOrigin.y.toFloat()
                )
                pointUp = DrawPoint(x, y)
            } else {
                pointDown = DrawPoint(
                    x,
                    pointOrigin.y.toFloat(),
                )
                pointUp = DrawPoint(
                    pointOrigin.x.toFloat(),
                    y,
                )
            }
        } else {
            if (x > pointOrigin.x) {
                pointDown = DrawPoint(
                    pointOrigin.x.toFloat(),
                    y,
                )
                pointUp = DrawPoint(
                    x, pointOrigin.y.toFloat()
                )
            } else {
                pointDown = DrawPoint(
                    x,
                    y,
                )
                pointUp = DrawPoint(
                    pointOrigin.x.toFloat(), pointOrigin.y.toFloat()
                )
            }
        }

        rectDst.set(
            pointDown.x, pointDown.y, pointUp.x, pointUp.y
        )

    }

    fun onActionUp(onUpdate: (DrawStickerModel) -> Unit) {
        data.pointUp = pointUp
        data.pointDown = pointDown

        onUpdate(data)
    }
}
