package com.volio.draw.draw

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import com.volio.draw.model.DrawPoint
import com.volio.draw.model.PathDrawData

class DrawPath(
    var data: PathDrawData
) {
    private var paintPath: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        maskFilter = BlurMaskFilter(1f, BlurMaskFilter.Blur.NORMAL)
    }

    var listDrawPoint: MutableList<DrawPoint> = mutableListOf()

    fun onDraw(canvas: Canvas) {
        drawPathDefault(canvas, data)
    }

    fun onTouch(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                listDrawPoint = mutableListOf()
            }

            MotionEvent.ACTION_MOVE -> {
                listDrawPoint.add(DrawPoint(event.x, event.y))
                updatePath(data.path, listDrawPoint)
            }

            MotionEvent.ACTION_UP -> {

            }
        }
    }

    private fun updatePath(path: Path, listPoint: List<DrawPoint>) {
        path.reset()
        if (listPoint.isNotEmpty()) {
            var lastPoint = listPoint[0]
            path.moveTo(lastPoint.x, lastPoint.y)
            for (index in 1 until listPoint.size) {
                val point = listPoint[index]
                path.quadTo(
                    lastPoint.x,
                    lastPoint.y,
                    (point.x + lastPoint.x) / 2f,
                    (point.y + lastPoint.y) / 2f
                )
                lastPoint = point
            }
        }
    }

    private fun drawPathDefault(canvas: Canvas, dataPathDraw: PathDrawData) {
        paintPath.color = dataPathDraw.color
        paintPath.strokeWidth = dataPathDraw.size
        canvas.drawPath(dataPathDraw.path, paintPath)
    }
}