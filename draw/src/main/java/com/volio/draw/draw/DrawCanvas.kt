package com.volio.draw.draw

import android.graphics.Canvas
import android.view.MotionEvent
import com.volio.draw.model.TypeDraw

interface DrawCanvas {

    fun onDraw(canvas: Canvas)

    fun onTouch(event: MotionEvent)

    fun isActiveUndo(): Boolean

    fun isActiveRedo(): Boolean

    fun onUndo()

    fun onRedo()

    fun onClearAll()

    fun setSizePath(size: Float)

    fun setColorPath(color: Int)

    fun setTypeDraw(typeDraw: TypeDraw)

    fun zoomIn()

    fun zoomOut()

    fun showGrid(isShow: Boolean)

    fun setStickers(path: String)

}