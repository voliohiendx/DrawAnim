package com.volio.draw.draw

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import com.volio.draw.model.DrawPoint
import com.volio.draw.model.TypeCubes
import com.volio.draw.model.draw.DrawCubesModel

class DrawCubes(
    var data: DrawCubesModel,
) {
    private var paintDraw: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    var pointUp = DrawPoint(0f, 0f)
    var pointDown = DrawPoint(0f, 0f)

    init {
        pointDown = data.pointDown
        pointUp = data.pointUp
    }


    fun onDraw(canvas: Canvas) {
        paintDraw.color = data.color
        paintDraw.strokeWidth = data.size

        when (data.typeCubes) {
            TypeCubes.LINE -> {
                drawLine(canvas)
            }

            TypeCubes.CIRCLE -> {
                drawCircle(canvas)
            }

            TypeCubes.SQUARE -> {
                drawSquare(canvas)
            }
        }
    }

    fun drawLine(canvas: Canvas) {
        val path = Path()
        path.moveTo(pointDown.x, pointDown.y)
        path.quadTo(
            pointDown.x, pointDown.y, (pointUp.x + pointDown.x) / 2f, (pointUp.y + pointDown.y) / 2f
        )

        canvas.drawPath(
            path, paintDraw
        )
    }

    fun drawSquare(canvas: Canvas) {
        canvas.drawRect(
            Rect(pointDown.x.toInt(), pointDown.y.toInt(), pointUp.x.toInt(), pointUp.y.toInt()),
            paintDraw
        )
    }

    fun drawCircle(canvas: Canvas) {
        canvas.drawOval(
            RectF(pointDown.x, pointDown.y, pointUp.x, pointUp.y), paintDraw
        )
    }


    fun onActionDown(x: Float, y: Float) {
        pointDown = DrawPoint(x, y)
        pointUp = DrawPoint(x, y)
    }

    fun onActionMove(x: Float, y: Float) {
        pointUp = DrawPoint(x, y)
    }

    fun onActionUp(onUpdate: (DrawCubesModel) -> Unit) {
        data.pointUp = pointUp
        data.pointDown = pointDown

        onUpdate(data)
    }
}