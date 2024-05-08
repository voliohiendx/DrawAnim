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

    var typeDraw: TypeDraw = TypeDraw.STICKER

    val dataDraw = mutableListOf<DataDraw>()
    private var listPath: MutableList<DrawPath> = mutableListOf()
    private var listSticker: MutableList<DrawSticker> = mutableListOf()

    private var currentPath: DrawPath = DrawPath(PathDrawData(0, Path(), 10f, Color.BLACK, 0))

    private var currentSticker: DrawSticker = DrawSticker(context, DrawStickerModel(System.currentTimeMillis(), "https://e0.pxfuel.com/wallpapers/353/461/desktop-wallpaper-luffy-g5-gomu-no-nika-monkey-akuma-gear.jpg", DrawPoint(0f, 0f), DrawPoint(0f, 0f)))


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
        currentSticker.onDraw(canvas)
        currentPath.onDraw(canvas)

    }

    override fun onTouch(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                if (typeDraw == TypeDraw.BRUSH) {
                    dataDraw.add(DrawPathModel(System.currentTimeMillis(), currentPath.listDrawPoint, currentPath.data.size, currentPath.data.color, currentPath.data.brushType))
                    updateAllPath()
                } else if (typeDraw == TypeDraw.STICKER) {
                    currentSticker.onActionUp(event) {
                        dataDraw.add(it)
                    }
                    updateAllSticker()
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (typeDraw == TypeDraw.BRUSH) {
                    currentPath.onActionMove(event)
                } else {
                    currentSticker.onActionMove(event)
                }
            }

            MotionEvent.ACTION_DOWN -> {
                if (typeDraw == TypeDraw.BRUSH) {
                    currentPath.onActionDown(event)
                } else {
                    Log.d("HIIUIUIUIUI", "onTouch: ")
                    currentSticker = DrawSticker(context, DrawStickerModel(System.currentTimeMillis(), "https://e0.pxfuel.com/wallpapers/353/461/desktop-wallpaper-luffy-g5-gomu-no-nika-monkey-akuma-gear.jpg", DrawPoint(0f, 0f), DrawPoint(0f, 0f)))

                    currentSticker.onActionDown(event)
                }
            }
        }

        updateView.invoke()
    }

    override fun onUndo() {

    }

    override fun onRedo() {

    }

    override fun onClearAll() {

    }

    private fun updateAllPath(isUpdateView: Boolean = true) {
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
        if (isUpdateView) {
            updateView.invoke()
        }
    }

    fun updateAllSticker(isUpdateView: Boolean = true) {
        val listDrawNew: MutableList<DrawSticker> = mutableListOf()

        dataDraw.forEach {
            if (it is DrawStickerModel) {
                var sticker = getListStickerByData(it)
                if (sticker == null) {
                    sticker = DrawSticker(context, it)
                }
                listDrawNew.add(sticker)
            }
        }
        listSticker.clear()
        listSticker.addAll(listDrawNew)
        if (isUpdateView) {
            updateView.invoke()
        }
    }

    private fun getListPathByData(drawPath: DrawPathModel): DrawPath? {
        listPath.forEach {
            if (it.data.time == drawPath.time) {
                return it
            }
        }
        if (drawPath.time == currentPath.data.time) return DrawPath(currentPath.data.copy(path = Path(currentPath.data.path)))
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
                path.quadTo(lastPoint.x, lastPoint.y, (point.x + lastPoint.x) / 2f, (point.y + lastPoint.y) / 2f)
                lastPoint = point
            }
        }
    }


}