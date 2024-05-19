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
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import com.bumptech.glide.Glide
import com.volio.draw.model.ActionMode
import com.volio.draw.model.BrushType
import com.volio.draw.model.DataDraw
import com.volio.draw.model.draw.DrawPathModel
import com.volio.draw.model.DrawPoint
import com.volio.draw.model.FrameModel
import com.volio.draw.model.TypeCubes
import com.volio.draw.model.data.FillDrawData
import com.volio.draw.model.draw.DrawStickerModel
import com.volio.draw.model.data.PathDrawData
import com.volio.draw.model.TypeDraw
import com.volio.draw.model.draw.DrawCubesModel
import com.volio.draw.model.draw.DrawFillModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sqrt


class DrawLayout(val context: Context, private val updateView: () -> Unit) : DrawCanvas {

    private var matrix: Matrix = Matrix()
    private var matrixInvert: Matrix = Matrix()

    private var saveDrawBitmap: Pair<Int, Bitmap>? = null
    private var startDraw: Int = 0
    private var endDraw: Int = 0

    private var bitmapBackground: Bitmap? = null
    private var isShowGrid: Boolean = true

    private var rectBorder: RectF = RectF()
    private var rectAfter: RectF = RectF()
    private var paintBackground: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }

    private var paintGrid: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        color = Color.RED
        strokeWidth = 2f
    }

    private var paintBitmap: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val numberOfColumnCells = 12

    private var oldDistance = 0f
    private var lastDown = 0f
    private var midPoint = PointF()
    private var downX = 0f
    private var downY = 0f

    private var currentMode: ActionMode = ActionMode.NONE

    private var viewWidth: Float = 0f
    private var viewHeight: Float = 0f

    private var typeDraw: TypeDraw = TypeDraw.BRUSH
    private var typeCubes: TypeCubes = TypeCubes.CIRCLE

    private var frameData: FrameModel? = null

    // private var dataDraw = mutableListOf<DataDraw>()
    private val listUndo: ArrayDeque<DataDraw> = ArrayDeque()
    private val listRedo: ArrayDeque<DataDraw> = ArrayDeque()

    private var listPath: MutableList<DrawPath> = mutableListOf()
    private var listSticker: MutableList<DrawSticker> = mutableListOf()
    private var listCubes: MutableList<DrawCubes> = mutableListOf()
    private var listFill: MutableList<DrawFill> = mutableListOf()

    private var currentPath: PathDrawData = PathDrawData(System.currentTimeMillis(), Path(), 10f, Color.BLACK, BrushType.BRUSH)

    private var currentSticker: DrawStickerModel = DrawStickerModel(System.currentTimeMillis(), "https://png.pngtree.com/png-clipart/20231018/original/pngtree-cloud-cute-clouds-blue-sky-png-image_13356252.png", DrawPoint(0f, 0f), DrawPoint(0f, 0f))

    private var drawPath: DrawPath? = DrawPath(currentPath.copy())

    private var drawCubes: DrawCubes? = null

    private var drawSticker: DrawSticker? = DrawSticker(context, currentSticker, {})

    fun setData(data: FrameModel, pathBackground: String, width: Float, height: Float) {
        setViewSize(width, height)

        frameData = data
        saveDrawBitmap = null

        setBackground(pathBackground)
        updateBitmapCache()

        updateAllSticker()
        updateAllPath()
        updateAllFill()
        updateAllCubes()

        updateView.invoke()
    }

    fun getDataDraw(): FrameModel? = frameData

    fun setViewSize(width: Float, height: Float) {
        viewWidth = width
        viewHeight = height

        rectBorder = RectF(0f, 0f, viewWidth, viewHeight)

        //keo view ve giua
        matrix.reset()
        matrix.postTranslate(width / 2f - viewWidth / 2, height / 2f - viewHeight / 2)

        matrix.invert(matrixInvert)

    }

    fun setBackground(path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            bitmapBackground = Glide.with(context).asBitmap().load(path).submit().get()
            withContext(Dispatchers.Main) {
                updateView.invoke()
            }
        }
    }

    private fun updateBitmapCache() {
        frameData?.let {
            if (it.data.isEmpty()) return

            val indexSaveBitmap = ((it.data.size - 1) / 10) * 10

            startDraw = indexSaveBitmap
            endDraw = it.data.size - 1

            if (indexSaveBitmap > 0) {
                saveDrawBitmap?.let { data ->
                    if (data.first != indexSaveBitmap) {
                        saveDrawBitmap = drawBitmapCache(indexSaveBitmap)
                    }
                } ?: run {
                    saveDrawBitmap = drawBitmapCache(indexSaveBitmap)
                }
            } else {
                saveDrawBitmap = null
            }
        }
    }

    private fun drawBitmapCache(index: Int): Pair<Int, Bitmap> {
        val bitmapCache = Bitmap.createBitmap(viewWidth.toInt(), viewHeight.toInt(), Bitmap.Config.ARGB_8888)
        Canvas(bitmapCache).apply {
            drawViewDraw(this, 0, index)
        }
        return Pair(index, bitmapCache)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {

        canvas.save()
        canvas.setMatrix(matrix)
        canvas.clipRect(rectBorder)
        canvas.drawRect(rectBorder, paintBackground)

        bitmapBackground?.let { bitmapBackground ->
            canvas.drawBitmap(bitmapBackground, Rect(0, 0, bitmapBackground.width, bitmapBackground.height), rectBorder, paintBitmap)
        }
        canvas.drawBackground()
        if (isShowGrid) canvas.drawGrid()
        //tao 1 layer moi len tren
        val layerId = canvas.saveLayer(rectBorder, paintBackground)

        drawViewDraw(canvas, startDraw, endDraw)
        canvas.restoreToCount(layerId)
        canvas.restore()

    }

    private fun Canvas.drawBackground() {
        bitmapBackground?.let { bitmapBackground ->
            this.drawBitmap(bitmapBackground, Rect(0, 0, bitmapBackground.width, bitmapBackground.height), rectBorder, paintBitmap)
        }
    }

    private fun Canvas.drawGrid() {

        for (i in 0..numberOfColumnCells) {
            this.drawLine(i * (viewWidth / numberOfColumnCells).toFloat(), 0f, i * (viewWidth / numberOfColumnCells).toFloat(), viewHeight.toFloat(), paintGrid)
        }
        val draw = (viewHeight / (viewHeight / numberOfColumnCells)).toInt()

        for (i in 0..draw) {
            this.drawLine(0f, i * (viewHeight / draw).toFloat(), viewWidth.toFloat(), i * (viewHeight / draw).toFloat(),

                    paintGrid)
        }
    }

    fun drawViewDraw(canvas: Canvas, startDraw: Int, endDraw: Int) {

        saveDrawBitmap?.let { lastBitmap ->
            canvas.drawBitmap(lastBitmap.second, Rect(0, 0, lastBitmap.second.width, lastBitmap.second.height), rectBorder, paintBitmap)
        }

        frameData?.let {
            if (it.data.isNotEmpty()) {
                for (index in startDraw..endDraw) {
                    val data = it.data[index]
                    when (data) {
                        is DrawPathModel -> {
                            getListPathByData(data)?.onDraw(canvas)
                        }

                        is DrawStickerModel -> {
                            getListStickerByData(data)?.onDraw(canvas)
                        }

                        is DrawFillModel -> {
                            getListFillByData(data)?.onDraw(canvas)
                        }

                        is DrawCubesModel -> {
                            getListCubesByData(data)?.onDraw(canvas)
                        }

                        else -> {}
                    }

                }
            }
        }

        drawSticker?.onDraw(canvas)
        drawPath?.onDraw(canvas)
        drawCubes?.onDraw(canvas)
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
                        actionUpCubes()
                        actionUpFill(x, y)
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
                        actionMoveCubes(x, y)
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
                actionDownCubes(x, y)
                downX = event.x
                downY = event.y

                currentMode = ActionMode.POINTER_1
            }
        }

        updateView.invoke()
    }

    private fun actionUpFill(x: Float, y: Float) {
        if (x < 0 || y < 0) return

        if (typeDraw == TypeDraw.FILL) {
            val currentBitmap: Bitmap = Bitmap.createBitmap(viewWidth.toInt(), viewHeight.toInt(), Bitmap.Config.ARGB_8888)
            val canvasBitmap: Canvas = Canvas(currentBitmap)
            drawViewDraw(canvasBitmap, startDraw, endDraw)

            DrawFill(context, FillDrawData(System.currentTimeMillis(), currentBitmap, 0, 0, currentPath.color), viewWidth, viewHeight).apply {
                setFloodFill(x.toInt(), y.toInt(), currentPath.color) {
                    val drawFillModel = DrawFillModel(it.time, it.x, it.y, currentPath.color)
                    frameData?.data?.add(drawFillModel)
                    listFill.add(this)

                    listUndo.add(drawFillModel)
                    listRedo.clear()

                    updateBitmapCache()
                    updateAllFill()
                }
            }
        }
    }


    private fun actionMoveScale(event: MotionEvent) {
        val newDistance = calculateDistance(event)
        if (newDistance != 0f) {
            val fluctuationAmplitude = newDistance - lastDown
            if (fluctuationAmplitude < -80 || fluctuationAmplitude > 80) {

                midPoint = calculateMidPoint(event)
                matrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x, midPoint.y)
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
                matrix.postScale(scale, scale, midPoint.x, midPoint.y)
            }

            matrix.invert(matrixInvert)
        }
    }

    private fun actionDownSticker(x: Float, y: Float) {
        if (typeDraw == TypeDraw.STICKER) {
            drawSticker = DrawSticker(context, currentSticker.copy()) {
                updateView.invoke()
            }
            drawSticker?.onActionDown(x, y)
        }
    }

    private fun actionDownBrushErase() {
        if (typeDraw == TypeDraw.BRUSH || typeDraw == TypeDraw.ERASE) {
            drawPath = DrawPath(currentPath.copy())
            drawPath?.onActionDown()
        }
    }

    private fun actionDownCubes(x: Float, y: Float) {
        if (typeDraw == TypeDraw.CUBES) {
            drawCubes = DrawCubes(data = DrawCubesModel(System.currentTimeMillis(), DrawPoint(0f, 0f), DrawPoint(0f, 0f), currentPath.size, currentPath.color, BrushType.BRUSH, typeCubes))
            drawCubes?.onActionDown(x, y)
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

    private fun actionMoveCubes(x: Float, y: Float) {
        if (typeDraw == TypeDraw.CUBES) {
            drawCubes?.onActionMove(x, y)
        }
    }

    private fun actionUpBrushErase() {
        if (typeDraw == TypeDraw.BRUSH || typeDraw == TypeDraw.ERASE) {
            drawPath?.onActionUp {
                frameData?.data?.add(it)
                listPath.add(drawPath!!)
                drawPath = null

                listUndo.add(it)
                listRedo.clear()

                updateBitmapCache()
                updateAllPath()
            }
        }
    }

    private fun actionUpSticker() {
        if (typeDraw == TypeDraw.STICKER) {
            drawSticker?.onActionUp() {
                frameData?.data?.add(it)
                listSticker.add(drawSticker!!)
                drawSticker = null

                listUndo.add(it)
                listRedo.clear()

                updateBitmapCache()
                updateAllSticker()
            }
        }
    }

    private fun actionUpCubes() {
        if (typeDraw == TypeDraw.CUBES) {
            drawCubes?.onActionUp() {
                frameData?.data?.add(it)
                listCubes.add(drawCubes!!)
                drawCubes = null

                listUndo.add(it)
                listRedo.clear()

                updateBitmapCache()
                updateAllCubes()
            }
        }
    }

    override fun isActiveUndo(): Boolean = true

    override fun isActiveRedo(): Boolean = true

    override fun onUndo() {
        val data = listUndo.removeLastOrNull()
        if (data != null) {
            listRedo.add(data)
            frameData?.data?.remove(data)
        }
        updateBitmapCache()

        updateView.invoke()
    }

    override fun onRedo() {

        val data = listRedo.removeLastOrNull()
        if (data != null) {
            listUndo.add(data)
            frameData?.data?.add(data)
        }
        updateBitmapCache()

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

    override fun showGrid(isShow: Boolean) {
        isShowGrid = isShow
        updateView()
    }

    override fun setStickers(path: String) {
        typeDraw = TypeDraw.STICKER
        currentSticker = DrawStickerModel(System.currentTimeMillis(), path, DrawPoint(0f, 0f), DrawPoint(0f, 0f))
    }

    fun fillOn() {
        typeDraw = TypeDraw.FILL
    }

    fun cubesType(typeCubes: TypeCubes) {
        typeDraw = TypeDraw.CUBES
        this.typeCubes = typeCubes
    }

    private fun updateAllPath() {
        val listDrawNew: MutableList<DrawPath> = mutableListOf()

        frameData?.data?.forEach {
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

    private fun updateAllFill() {

        val listDrawNew: MutableList<DrawFill> = mutableListOf()

        frameData?.data?.forEach {
            if (it is DrawFillModel) {
                val draw = getListFillByData(it)
                if (draw == null) {
                    val currentBitmap: Bitmap = Bitmap.createBitmap(viewWidth.toInt(), viewHeight.toInt(), Bitmap.Config.ARGB_8888)
                    val canvasBitmap: Canvas = Canvas(currentBitmap)
                    drawViewDraw(canvasBitmap, startDraw, endDraw)

                    val drawFill = DrawFill(context, FillDrawData(it.time, currentBitmap, it.x, it.y, it.color), viewWidth, viewHeight)

                    drawFill.setFloodFill(it.x, it.y, it.color) {
                        drawFill.data = it
                        listDrawNew.add(drawFill)
                    }
                } else {
                    listDrawNew.add(draw)
                }
            }
        }

        listFill.clear()
        listFill.addAll(listDrawNew)

        updateView.invoke()
    }

    private fun updateAllSticker() {
        val listDrawNew: MutableList<DrawSticker> = mutableListOf()

        frameData?.data?.forEach {
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

    private fun updateAllCubes() {
        val listDrawNew: MutableList<DrawCubes> = mutableListOf()

        frameData?.data?.forEach {
            if (it is DrawCubesModel) {
                var cubes = getListCubesByData(it)
                if (cubes == null) {
                    cubes = DrawCubes(it)
                }
                listDrawNew.add(cubes)
            }
        }
        listCubes.clear()
        listCubes.addAll(listDrawNew)

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

    private fun getListCubesByData(drawCubesModel: DrawCubesModel): DrawCubes? {
        listCubes.forEach {
            if (it.data.time == drawCubesModel.time) {
                return it
            }
        }

        return null
    }

    private fun getListFillByData(drawFill: DrawFillModel): DrawFill? {
        listFill.forEach {
            if (it.data.time == drawFill.time) {
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