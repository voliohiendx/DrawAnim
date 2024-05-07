package com.volio.draw.draw

import android.graphics.Canvas
import android.view.MotionEvent

interface DrawCanvas {

    fun onDraw(canvas: Canvas)

    fun onTouch(event: MotionEvent)

    fun onUndo()

    fun onRedo()

    fun onClearAll()
}