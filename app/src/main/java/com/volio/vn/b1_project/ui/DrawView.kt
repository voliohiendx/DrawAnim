package com.volio.vn.b1_project.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.bumptech.glide.Glide
import com.volio.vn.b1_project.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DrawView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private var paintBitmap: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bitmap: Bitmap? = null
    private var rectScr: Rect = Rect()
    private var rectDst: RectF = RectF()

    init {
        loadImage()
    }

    private fun loadImage() {
        CoroutineScope(Dispatchers.IO).launch {
            bitmap = Glide.with(context).asBitmap()
                .load("https://e0.pxfuel.com/wallpapers/353/461/desktop-wallpaper-luffy-g5-gomu-no-nika-monkey-akuma-gear.jpg")
                .submit().get()

            bitmap?.let {
                rectScr.set(0, 0, it.width, it.height)
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.d("HIUIUIUIUIU", "onDraw: ")
        bitmap?.let {
            Log.d("HIUIUIUIUIU", "onDraw: ")
            canvas.drawBitmap(it, rectScr, rectDst, paintBitmap)
        }

    }

    var pointOrigin = Point(0, 0)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {

            }

            MotionEvent.ACTION_MOVE -> {
                rectDst.set(
                    pointOrigin.x.toFloat(),
                    pointOrigin.y.toFloat(),
                    event.x- pointOrigin.x,
                    event.y- pointOrigin.y
                )
            }

            MotionEvent.ACTION_DOWN -> {
                pointOrigin= Point(event.x.toInt(), event.y.toInt())
            }

        }
        postInvalidate()
        return true
    }


}