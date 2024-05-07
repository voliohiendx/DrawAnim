package com.volio.vn.data.models.canvas

import android.graphics.Canvas

interface DrawCanvas {

    fun onDraw(canvas: Canvas)

    fun onTouch(x: Float, y: Float, action: Int)

    fun onUndo()

    fun onRedo()

    fun onClearAll()
}