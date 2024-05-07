package com.volio.vn.b1_project.ui.splash

import android.graphics.Bitmap
import android.graphics.Color
import java.util.LinkedList
import java.util.Queue

class QueueLinearFloodFiller(
    img: Bitmap,
    targetColor: Int,
    newColor: Int
) {
    var image: Bitmap? = null
    var tolerance: IntArray = intArrayOf(0, 0, 0)
    private var width: Int = 0
    private var height: Int = 0
    var fillColor: Int = 0
    private var startColor: IntArray = intArrayOf(0, 0, 0)
    private lateinit var pixelsChecked: BooleanArray
    private lateinit var ranges: Queue<FloodFillRange>
    private lateinit var pixels: IntArray


    init {
        useImage(img)
        fillColor = newColor
        setTargetColor(targetColor)
    }

    private fun useImage(img: Bitmap) {
        width = img.width
        height = img.height
        image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        pixels = IntArray(width * height)

        img.getPixels(pixels, 0, width, 1, 1, width - 1, height - 1)
    }

    private fun setTargetColor(targetColor: Int) {
        startColor[0] = Color.red(targetColor)
        startColor[1] = Color.green(targetColor)
        startColor[2] = Color.blue(targetColor)
    }


    private fun prepare() {
        pixelsChecked = BooleanArray(pixels.size)
        ranges = LinkedList<FloodFillRange>()
    }

    fun floodFill(x: Int, y: Int) {
        prepare()

        if (startColor[0] == 0) {
            val startPixel = pixels[width * y + x]
            startColor[0] = (startPixel shr 16) and 0xff
            startColor[1] = (startPixel shr 8) and 0xff
            startColor[2] = startPixel and 0xff
        }

        linearFill(x, y)

        var range: FloodFillRange

        while (ranges.isNotEmpty()) {
            range = ranges.remove()

            var downPxIdx = (width * (range.Y + 1)) + range.startX
            var upPxIdx = (width * (range.Y - 1)) + range.startX
            val upY = range.Y - 1
            val downY = range.Y + 1

            for (i in range.startX..range.endX) {
                if (range.Y > 0 && (!pixelsChecked[upPxIdx])
                    && checkPixel(upPxIdx)
                ) linearFill(i, upY)

                if (range.Y < (height - 1) && (!pixelsChecked[downPxIdx])
                    && checkPixel(downPxIdx)
                ) linearFill(i, downY)

                downPxIdx++
                upPxIdx++
            }
        }

        image?.setPixels(pixels, 0, width, 1, 1, width - 1, height - 1)
    }

    private fun linearFill(x: Int, y: Int) {
        var lFillLoc = x
        var pxIdx = (width * y) + x

        while (true) {
            pixels[pxIdx] = fillColor
            pixelsChecked[pxIdx] = true

            lFillLoc--
            pxIdx--

            if (lFillLoc < 0 || (pixelsChecked[pxIdx]) || !checkPixel(pxIdx)) {
                break
            }
        }
        lFillLoc++


        var rFillLoc = x
        pxIdx = (width * y) + x

        while (true) {
            pixels[pxIdx] = fillColor
            pixelsChecked[pxIdx] = true

            rFillLoc++
            pxIdx++

            if (rFillLoc >= width || pixelsChecked[pxIdx] || !checkPixel(pxIdx)) {
                break
            }
        }
        rFillLoc--

        ranges.offer(FloodFillRange(lFillLoc, rFillLoc, y))
    }

    private fun checkPixel(px: Int): Boolean {
        val red = (pixels[px] ushr 16) and 0xff
        val green = (pixels[px] ushr 8) and 0xff
        val blue = pixels[px] and 0xff

        return (red >= (startColor[0] - tolerance[0]) && red <= (startColor[0] + tolerance[0]) && green >= (startColor[1] - tolerance[1]) && green <= (startColor[1] + tolerance[1]) && blue >= (startColor[2] - tolerance[2]) && blue <= (startColor[2] + tolerance[2]))
    }

    class FloodFillRange(var startX: Int, var endX: Int, var Y: Int)
}