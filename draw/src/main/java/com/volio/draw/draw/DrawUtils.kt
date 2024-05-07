package com.volio.draw.draw

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import com.volio.draw.model.DataDraw

class DrawUtils(
    private val context: Context, private val update: () -> Unit = {}
) {

    private val drawPath = DrawLayout(context) {
        update()
    }

    fun setViewSize(width: Int, height: Int) {

    }

    fun draw(canvas: Canvas) {
        drawPath.onDraw(canvas)
    }

    fun touch(event: MotionEvent) {
        drawPath.onTouch(event)
    }

}