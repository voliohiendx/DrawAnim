package com.volio.draw.draw

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.view.MotionEvent
import com.volio.draw.model.BrushType
import com.volio.draw.model.DrawPathModel
import com.volio.draw.model.DrawPoint
import com.volio.draw.model.DrawStickerModel
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

    private var paintErase: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        maskFilter = BlurMaskFilter(2f, BlurMaskFilter.Blur.NORMAL)
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

    }

    var listDrawPoint: MutableList<DrawPoint> = mutableListOf()

    fun onDraw(canvas: Canvas) {
        drawPath(canvas, data)
    }

    fun onActionDown() {
        listDrawPoint = mutableListOf()
    }

    fun onActionMove(x: Float, y: Float) {
        listDrawPoint.add(DrawPoint(x, y))
        updatePath(data.path, listDrawPoint)
    }

    fun onActionUp(onUpdate: (DrawPathModel) -> Unit) {
        onUpdate(
            DrawPathModel(
                System.currentTimeMillis(),
                listDrawPoint,
                data.size,
                data.color,
                data.brushType
            )
        )
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

    fun drawPath(canvas: Canvas, dataPathDraw: PathDrawData) {
        paintPath.color = dataPathDraw.color
        paintPath.strokeWidth = dataPathDraw.size
        paintErase.strokeWidth = dataPathDraw.size
        canvas.drawPath(
            dataPathDraw.path,
            if (dataPathDraw.brushType == BrushType.ERASE) paintErase else paintPath
        )

    }

}