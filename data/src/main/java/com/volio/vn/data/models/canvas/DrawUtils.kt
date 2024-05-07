package com.volio.vn.data.models.canvas

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent

class DrawUtils(
    private val context: Context, private val update: () -> Unit = {}
) {

    private val drawPath = DrawPath {
        update()
    }

    fun setViewSize(width: Int, height: Int) {

    }

    fun draw(canvas: Canvas) {
        drawPath.onDraw(canvas)
    }

    fun touch(event: MotionEvent) {
        val touch: FloatArray = floatArrayOf(event.x, event.y)
        drawPath.onTouch(touch[0], touch[1], event.action)
    }

}