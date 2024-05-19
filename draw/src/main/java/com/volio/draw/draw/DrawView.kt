package com.volio.draw.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.volio.draw.model.DataDraw
import com.volio.draw.model.FrameModel
import com.volio.draw.model.TypeCubes
import com.volio.draw.model.TypeDraw


class DrawView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        // set ratio o day nha
    }


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

    fun setData(frameModel: FrameModel, ratio: Float, pathBackground: String) {
        val widthView = (width * 0.8f)
        drawPath.setData(frameModel, pathBackground, widthView, widthView * ratio)
    }

    fun setBrushSize(size: Float) {
        drawPath.setSizePath(size)
    }

    fun setBrushColor(color: Int) {
        drawPath.setColorPath(color)
    }


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

    fun zoomIn() {
        drawPath.zoomIn()
    }

    fun zoomOut() {
        drawPath.zoomOut()
    }

    fun fillOn() {
        drawPath.fillOn()

    }

    fun cubes(typeCubes: TypeCubes) {
        drawPath.cubesType(typeCubes)
    }

//    fun setBackgroundBitmap(path: String) {
//        drawPath.setBackground(path)
//    }

    fun showGrid(show: Boolean) {
        drawPath.showGrid(show)
    }

    fun setStickers(path: String) {
        drawPath.setStickers(path)
    }


}