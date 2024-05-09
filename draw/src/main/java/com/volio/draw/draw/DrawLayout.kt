package com.volio.draw.draw

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import com.volio.draw.model.ActionMode
import com.volio.draw.model.BrushType
import com.volio.draw.model.DataDraw
import com.volio.draw.model.DrawPathModel
import com.volio.draw.model.DrawPoint
import com.volio.draw.model.DrawStickerModel
import com.volio.draw.model.PathDrawData
import com.volio.draw.model.TypeDraw
import kotlin.math.sqrt


class DrawLayout(val context: Context, private val updateView: () -> Unit) : DrawCanvas {

    private var matrix: Matrix = Matrix()
    private var matrixInvert: Matrix = Matrix()

    private var rectBorder: RectF = RectF()
    private var rectAfter: RectF = RectF()
    private var paintBackground: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }
    private var oldDistance = 0f
    private var lastDown = 0f
    private var midPoint = PointF()
    private var downX = 0f
    private var downY = 0f

    private var currentMode: ActionMode = ActionMode.NONE

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    private var typeDraw: TypeDraw = TypeDraw.BRUSH

    private var dataDraw = mutableListOf<DataDraw>()
    private val listUndo: ArrayDeque<DataDraw> = ArrayDeque()
    private val listRedo: ArrayDeque<DataDraw> = ArrayDeque()

    private var listPath: MutableList<DrawPath> = mutableListOf()
    private var listSticker: MutableList<DrawSticker> = mutableListOf()

    private var currentPath: PathDrawData =
        PathDrawData(System.currentTimeMillis(), Path(), 10f, Color.BLACK, BrushType.BRUSH)

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

    fun setViewSize(width: Int, height: Int) {
        viewWidth = width
        viewHeight = height
        rectBorder = RectF(0f, 0f, width.toFloat(), height.toFloat())
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {

        canvas.save()
        canvas.setMatrix(matrix)
        canvas.clipRect(rectBorder)
        canvas.drawRect(rectBorder, paintBackground)

        //tao 1 layer moi len tren
        val layerId = canvas.saveLayer(rectBorder, paintBackground)

        drawViewDraw(canvas)
        canvas.restoreToCount(layerId)
        canvas.restore()

    }

    private fun drawViewDraw(canvas: Canvas) {
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

        val touch: FloatArray = floatArrayOf(event.x, event.y)
        matrixInvert.mapPoints(touch)

        val x = touch[0]
        val y = touch[1]

        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                lastDown = calculateDistance(event)
                oldDistance = calculateDistance(event)
                midPoint = calculateMidPoint(event)

                currentMode = ActionMode.POINTER_2
                drawSticker = null
                drawPath = null
            }


            MotionEvent.ACTION_UP -> {
                when (currentMode) {
                    ActionMode.POINTER_1 -> {
                        actionUpBrushErase()
                        actionUpSticker()
                    }

                    ActionMode.POINTER_2 -> {

                    }

                    else -> {}
                }
                currentMode = ActionMode.NONE
            }

            MotionEvent.ACTION_MOVE -> {
                when (currentMode) {
                    ActionMode.POINTER_1 -> {
                        actionMoveBrushErase(x, y)
                        actionMoveSticker(x, y)
                    }

                    ActionMode.POINTER_2 -> {
                        actionMoveScale(event)
                    }

                    else -> {}
                }
            }

            MotionEvent.ACTION_DOWN -> {
                actionDownBrushErase()
                actionDownSticker(x, y)
                downX = event.x
                downY = event.y

                currentMode = ActionMode.POINTER_1
            }
        }

        updateView.invoke()
    }

    private fun actionMoveScale(event: MotionEvent) {
        val newDistance = calculateDistance(event)
        if (newDistance != 0f) {
            val fluctuationAmplitude = newDistance - lastDown
            if (fluctuationAmplitude < -80 || fluctuationAmplitude > 80) {

                midPoint = calculateMidPoint(event)
                matrix.postScale(
                    newDistance / oldDistance,
                    newDistance / oldDistance,
                    midPoint.x,
                    midPoint.y
                )
            } else {
                matrix.postTranslate(event.x - downX, event.y - downY)
            }

            matrix.mapRect(rectAfter, rectBorder)

            oldDistance = calculateDistance(event)
            midPoint = calculateMidPoint(event)

            downX = event.x
            downY = event.y

            if (rectAfter.width() < rectBorder.width()) {
                val scale = rectBorder.width() / rectAfter.width()
                matrix.postScale(
                    scale, scale, midPoint.x,
                    midPoint.y
                )
            }

            matrix.invert(matrixInvert)
        }
    }

    private fun actionDownSticker(x: Float, y: Float) {
        if (typeDraw == TypeDraw.STICKER) {
            drawSticker = DrawSticker(
                context,
                currentSticker.copy(), {
                    updateView.invoke()
                }
            )
            drawSticker?.onActionDown(x, y)
        }
    }

    private fun actionDownBrushErase() {
        if (typeDraw == TypeDraw.BRUSH || typeDraw == TypeDraw.ERASE) {
            drawPath = DrawPath(currentPath.copy())
            drawPath?.onActionDown()
        }
    }

    private fun actionMoveSticker(x: Float, y: Float) {
        if (typeDraw == TypeDraw.STICKER) {
            drawSticker?.onActionMove(x, y)
        }
    }

    private fun actionMoveBrushErase(x: Float, y: Float) {
        if (typeDraw == TypeDraw.BRUSH || typeDraw == TypeDraw.ERASE) {
            drawPath?.onActionMove(x, y)
        }
    }

    private fun actionUpBrushErase() {
        if (typeDraw == TypeDraw.BRUSH || typeDraw == TypeDraw.ERASE) {
            drawPath?.onActionUp {
                dataDraw.add(it)
                listPath.add(drawPath!!)
                drawPath = null

                listUndo.add(it)
                listRedo.clear()
                updateAllPath()
            }
        }
    }

    private fun actionUpSticker() {
        if (typeDraw == TypeDraw.STICKER) {
            drawSticker?.onActionUp() {
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
                currentPath.brushType = BrushType.BRUSH
            }

            TypeDraw.ERASE -> {
                currentPath.brushType = BrushType.ERASE
            }

            TypeDraw.STICKER -> {

            }

            else -> {}
        }
    }

    override fun zoomIn() {
        matrix.setScale(2f, 2f, viewWidth / 2f, viewHeight / 2f)
        matrix.invert(matrixInvert)
        updateView()

    }

    override fun zoomOut() {
        matrix.setScale(1f, 1f, viewWidth / 2f, viewHeight / 2f)
        matrix.invert(matrixInvert)
        updateView()
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

    private fun calculateDistance(event: MotionEvent): Float {
        if (event.pointerCount < 2) {
            return 0f
        }
        return calculateDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1))
    }

    private fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val x = (x1 - x2).toDouble()
        val y = (y1 - y2).toDouble()

        return sqrt(x * x + y * y).toFloat()
    }

    private fun calculateMidPoint(event: MotionEvent?): PointF {
        if (event == null || event.pointerCount < 2) {
            midPoint[0f] = 0f
            return midPoint
        }
        val x = (event.getX(0) + event.getX(1)) / 2
        val y = (event.getY(0) + event.getY(1)) / 2
        midPoint[x] = y
        return midPoint
    }

}