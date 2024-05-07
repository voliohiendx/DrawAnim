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
    private var listPath: MutableList<PathDrawData> = mutableListOf()

    private var currentPath: DrawPath = DrawPath(PathDrawData(0, Path(), 10f, Color.BLACK, 0))

    private var currentSticker: DrawSticker = DrawSticker(
        context, DrawStickerModel(
            System.currentTimeMillis(),
            "https://e0.pxfuel.com/wallpapers/353/461/desktop-wallpaper-luffy-g5-gomu-no-nika-monkey-akuma-gear.jpg",
            DrawPoint(0f, 0f),
            DrawPoint(0f, 0f)
        )
    )


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {

        dataDraw.forEach { data ->
            if (data is DrawPathModel) {
                getListPathByData(data)?.let {
                    DrawPath(it).onDraw(canvas)
                }
            } else {
                if (data is DrawStickerModel) {
                    Log.d("HIUIUIUIUIUIUIII", "onDraw: ")
                    DrawSticker(context, data).onDraw(canvas)
                }
            }
        }
        currentSticker.onDraw(canvas)
        currentPath.onDraw(canvas)

    }

    override fun onTouch(event: MotionEvent) {
        if (typeDraw == TypeDraw.STICKER) {
            currentSticker.onTouch(event)
        } else {
            currentPath.onTouch(event)
        }
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                if (typeDraw == TypeDraw.BRUSH) {
                    dataDraw.add(
                        DrawPathModel(
                            System.currentTimeMillis(),
                            currentPath.listDrawPoint,
                            currentPath.data.size,
                            currentPath.data.color,
                            currentPath.data.brushType
                        )
                    )
                    updateAllPath()
                } else if (typeDraw == TypeDraw.STICKER) {
                    dataDraw.add(currentSticker.data)
                    Log.d("HIUIUIUUIUI", "onTouch: "+dataDraw.size)
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
        val listDrawNew: MutableList<PathDrawData> = mutableListOf()

        dataDraw.forEach {
            if (it is DrawPathModel) {
                var draw = getListPathByData(it)
                if (draw == null) {
                    val path = Path()
                    updatePath(path, it.listPoint)
                    draw = PathDrawData(
                        it.time, path, it.size, it.color, it.brushType
                    )
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

    private fun getListPathByData(drawPath: DrawPathModel): PathDrawData? {
        listPath.forEach {
            if (it.time == drawPath.time) {
                return it
            }
        }
        if (drawPath.time == currentPath.data.time) return currentPath.data.copy(
            path = Path(
                currentPath.data.path
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


}