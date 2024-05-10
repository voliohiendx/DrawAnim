package com.volio.draw

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun Bitmap.saveBitmapToInternalStorage(context: Context, filename: String): String? {

    val storageDir = context.filesDir

    val imageFile = File(storageDir, filename)

    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(imageFile)
        this.compress(Bitmap.CompressFormat.PNG, 100, fos)
        return imageFile.absolutePath
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    } finally {
        fos?.close()
    }

}