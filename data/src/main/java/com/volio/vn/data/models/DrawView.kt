package com.volio.vn.data.models

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.volio.vn.data.models.canvas.DrawUtils


class DrawView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    val drawUtils = DrawUtils(context) {
        postInvalidate()
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
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
        drawUtils.setViewSize(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        drawUtils.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        drawUtils.touch(event)
        return true
    }

}