package com.volio.draw.draw

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.volio.draw.model.DataDraw
import com.volio.draw.model.TypeDraw


class DrawView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    val drawPath = DrawLayout(context) {
        postInvalidate()
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            if (event.x >= 0.96f * width || event.x < 0.04 * width || event.y > 0.98 * height || event.y < 0.02f) {
                return false
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        // drawUtils.setViewSize(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        drawPath.onDraw(canvas)


    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        drawPath.onTouch(event)
        return true
    }

    fun setTypeDraw(typeDraw: TypeDraw) {
        drawPath.setTypeDraw(typeDraw)
    }

    fun setData(dataDraw: List<DataDraw>) {
        drawPath.setData(dataDraw)
    }

    fun setBrushSize(size: Float) {
        drawPath.setSizePath(size)
    }

    fun setBrushColor(color: Int) {
        drawPath.setColorPath(color)
    }

    fun getDataDraw(): List<DataDraw> = drawPath.getDataDraw()

    fun isActiveUndo(): Boolean {
        return drawPath.isActiveUndo()
    }

    fun isActiveRedo(): Boolean {
        return drawPath.isActiveRedo()
    }

    fun undo() {
        drawPath.onUndo()
    }

    fun redo() {
        drawPath.onRedo()
    }

}