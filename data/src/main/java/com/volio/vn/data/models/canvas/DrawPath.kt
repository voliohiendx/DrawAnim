package com.volio.vn.data.models.canvas

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import com.volio.vn.data.models.draw.DataPathDraw
import com.volio.vn.data.models.draw.DrawPath
import com.volio.vn.data.models.draw.DrawPoint

class DrawPath(private val updateView: () -> Unit) : DrawCanvas {

    private val listUndo: ArrayDeque<DrawPath> = ArrayDeque()
    private val listRedo: ArrayDeque<DrawPath> = ArrayDeque()

    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        maskFilter = BlurMaskFilter(1f, BlurMaskFilter.Blur.NORMAL)
    }

    private var listDrawPoint: MutableList<DrawPoint> = mutableListOf()
    private var listDrawPathData: MutableList<DrawPath> = mutableListOf()

    private var listDraw: MutableList<DataPathDraw> = mutableListOf()
    private var currentPath: DataPathDraw =
        DataPathDraw(0, Path(), 10f, Color.BLACK, false, 0)


    override fun onDraw(canvas: Canvas) {
        listDraw.forEach {
            drawPathDefault(canvas, it)
        }
        drawPathDefault(canvas, currentPath)
    }

    override fun onTouch(x: Float, y: Float, action: Int) {
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                listDrawPoint = mutableListOf()
            }

            MotionEvent.ACTION_MOVE -> {
                listDrawPoint.add(DrawPoint(x, y))
                updatePath(currentPath.path, listDrawPoint)
            }

            MotionEvent.ACTION_UP -> {
                val drawPath = DrawPath(
                    System.currentTimeMillis(),
                    listDrawPoint,
                    currentPath.size,
                    currentPath.color,
                    currentPath.isErase,
                    currentPath.brushType
                )
                listDrawPathData.add(drawPath)
                listUndo.add(drawPath)
                listRedo.clear()
                updateAllPath(false)
                currentPath.path.reset()
                // updateListPath.invoke(listDrawPathData)
            }
        }
        updateView.invoke()
    }

    override fun onUndo() {
        TODO("Not yet implemented")
    }

    override fun onRedo() {
        TODO("Not yet implemented")
    }

    override fun onClearAll() {
        TODO("Not yet implemented")
    }

    fun setData(listPath: List<DrawPath>) {
        listDrawPathData.clear()
        listDrawPathData.addAll(listPath)
        listDrawPathData.sortBy { it.timeCreated }
        updateAllPath()
    }

    private fun updateAllPath(isUpdateView: Boolean = true) {
        val listDrawNew: MutableList<DataPathDraw> = mutableListOf()
        listDrawPathData.forEach {
            var draw = getListPathByData(it)
            if (draw == null) {
                val path = Path()
                updatePath(path, it.listPoint)
                draw = DataPathDraw(
                    it.timeCreated,
                    path,
                    it.size,
                    it.color,
                    it.isErase,
                    it.brushType
                )
            }
            listDrawNew.add(draw)
        }
        listDraw.clear()
        listDraw.addAll(listDrawNew)
        if (isUpdateView) {
            updateView.invoke()
        }
    }

    private fun getListPathByData(drawPath: DrawPath): DataPathDraw? {
        listDraw.forEach {
            if (it.time == drawPath.timeCreated) {
                return it
            }
        }
        if (drawPath.timeCreated == currentPath.time) return currentPath.copy(
            path = Path(
                currentPath.path
            )
        )
        return null
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

    private fun drawPathDefault(canvas: Canvas, dataPathDraw: DataPathDraw) {
        paint.color = dataPathDraw.color
        paint.strokeWidth = dataPathDraw.size
        canvas.drawPath(dataPathDraw.path, paint)
    }


}