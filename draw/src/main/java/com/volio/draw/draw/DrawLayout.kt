package com.volio.draw.draw

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.util.Log
import android.view.MotionEvent
import com.volio.draw.model.DataDraw
import com.volio.draw.model.DrawPathModel
import com.volio.draw.model.DrawPoint
import com.volio.draw.model.DrawStickerModel
import com.volio.draw.model.PathDrawData
import com.volio.draw.model.TypeDraw

class DrawLayout(val context: Context, private val updateView: () -> Unit) : DrawCanvas {

    private var typeDraw: TypeDraw = TypeDraw.BRUSH

    private var dataDraw = mutableListOf<DataDraw>()
    private val listUndo: ArrayDeque<DataDraw> = ArrayDeque()
    private val listRedo: ArrayDeque<DataDraw> = ArrayDeque()

    private var listPath: MutableList<DrawPath> = mutableListOf()
    private var listSticker: MutableList<DrawSticker> = mutableListOf()

    private var currentPath: PathDrawData =
        PathDrawData(System.currentTimeMillis(), Path(), 10f, Color.BLACK, 0)

    private var currentSticker: DrawStickerModel = DrawStickerModel(
        System.currentTimeMillis(),
        "https://png.pngtree.com/png-clipart/20231018/original/pngtree-cloud-cute-clouds-blue-sky-png-image_13356252.png",
        DrawPoint(0f, 0f),
        DrawPoint(0f, 0f)
    )

    private var drawPath: DrawPath? = DrawPath(currentPath.copy())

    private var drawSticker: DrawSticker? = DrawSticker(
        context,
        currentSticker, {}
    )

    fun setData(dataDraw: List<DataDraw>) {
        this.dataDraw = dataDraw.toMutableList()
        updateAllSticker()
        updateAllPath()
        updateView.invoke()
    }

    fun getDataDraw(): List<DataDraw> = dataDraw

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        dataDraw.forEachIndexed { index, data ->
            if (data is DrawPathModel) {
                getListPathByData(data)?.onDraw(canvas)
            } else {
                if (data is DrawStickerModel) {
                    getListStickerByData(data)?.onDraw(canvas)
                }
            }
        }

        drawSticker?.onDraw(canvas)
        drawPath?.onDraw(canvas)

    }

    override fun onTouch(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                actionUpBrushErase(event)
                actionUpSticker(event)
            }

            MotionEvent.ACTION_MOVE -> {
                actionMoveBrushErase(event)
                actionMoveSticker(event)
            }

            MotionEvent.ACTION_DOWN -> {
                actionDownBrushErase(event)
                actionDownSticker(event)
            }
        }

        updateView.invoke()
    }

    private fun actionDownSticker(event: MotionEvent) {
        if (typeDraw == TypeDraw.STICKER) {
            drawSticker = DrawSticker(
                context,
                currentSticker.copy(), {
                    updateView.invoke()
                }
            )
            drawSticker?.onActionDown(event)
        }
    }

    private fun actionDownBrushErase(event: MotionEvent) {
        if (typeDraw == TypeDraw.BRUSH || typeDraw == TypeDraw.ERASE) {
            drawPath = DrawPath(currentPath.copy())
            drawPath?.onActionDown(event)
        }
    }

    private fun actionMoveSticker(event: MotionEvent) {
        if (typeDraw == TypeDraw.STICKER) {
            drawSticker?.onActionMove(event)
        }
    }

    private fun actionMoveBrushErase(event: MotionEvent) {
        if (typeDraw == TypeDraw.BRUSH || typeDraw == TypeDraw.ERASE) {
            drawPath?.onActionMove(event)
        }
    }

    private fun actionUpBrushErase(event: MotionEvent) {
        if (typeDraw == TypeDraw.BRUSH || typeDraw == TypeDraw.ERASE) {
            drawPath?.onActionUp(event) {
                dataDraw.add(it)
                listPath.add(drawPath!!)
                drawPath = null

                listUndo.add(it)
                listRedo.clear()
                updateAllPath()
            }
        }
    }

    private fun actionUpSticker(event: MotionEvent) {
        if (typeDraw == TypeDraw.STICKER) {
            drawSticker?.onActionUp(event) {
                dataDraw.add(it)
                listSticker.add(drawSticker!!)
                drawSticker = null

                listUndo.add(it)
                listRedo.clear()
                updateAllSticker()
            }
        }
    }

    override fun isActiveUndo(): Boolean = true

    override fun isActiveRedo(): Boolean = true

    override fun onUndo() {
        val data = listUndo.removeLastOrNull()
        if (data != null) {
            listRedo.add(data)
            dataDraw.remove(data)
        }

        updateView.invoke()
    }

    override fun onRedo() {

        val data = listRedo.removeLastOrNull()
        if (data != null) {
            listUndo.add(data)
            dataDraw.add(data)
        }

        updateView.invoke()
    }

    override fun onClearAll() {

    }

    override fun setSizePath(size: Float) {
        currentPath.size = size
    }

    override fun setColorPath(color: Int) {
        currentPath.color = color
    }

    override fun setTypeDraw(typeDraw: TypeDraw) {
        this.typeDraw = typeDraw
        when (typeDraw) {
            TypeDraw.BRUSH -> {

            }

            TypeDraw.ERASE -> {
                currentPath.color = Color.WHITE
            }

            TypeDraw.STICKER -> {

            }

            else -> {}
        }
    }

    private fun updateAllPath() {
        val listDrawNew: MutableList<DrawPath> = mutableListOf()

        dataDraw.forEach {
            if (it is DrawPathModel) {
                var draw = getListPathByData(it)
                if (draw == null) {
                    val path = Path()
                    updatePath(path, it.listPoint)
                    draw = DrawPath(PathDrawData(it.time, path, it.size, it.color, it.brushType))
                }
                listDrawNew.add(draw)
            }
        }
        listPath.clear()
        listPath.addAll(listDrawNew)

        updateView.invoke()
    }

    private fun updateAllSticker() {
        val listDrawNew: MutableList<DrawSticker> = mutableListOf()

        dataDraw.forEach {
            if (it is DrawStickerModel) {
                var sticker = getListStickerByData(it)
                if (sticker == null) {
                    sticker = DrawSticker(context, it, {
                        updateView.invoke()
                    })
                }
                listDrawNew.add(sticker)
            }
        }
        Log.d("HIUIUIUIUIU", "updateAllSticker: " + listDrawNew.size)
        listSticker.clear()
        listSticker.addAll(listDrawNew)

        updateView.invoke()

    }

    private fun getListPathByData(drawPath: DrawPathModel): DrawPath? {
        listPath.forEach {
            if (it.data.time == drawPath.time) {
                return it
            }
        }

        return null
    }

    private fun getListStickerByData(drawSticker: DrawStickerModel): DrawSticker? {
        listSticker.forEach {
            if (it.data.time == drawSticker.time) {
                return it
            }
        }

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


}