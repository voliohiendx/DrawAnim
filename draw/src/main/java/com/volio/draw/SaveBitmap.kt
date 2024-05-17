package com.volio.draw

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun Bitmap.saveBitmapToInternalStorage(context: Context, filename: String): String? {

    val imageFile = File(getDirTemp(context), filename)

    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(imageFile)
        this.compress(Bitmap.CompressFormat.PNG, 80, fos)
        return imageFile.absolutePath
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    } finally {
        fos?.close()
    }

}

fun getDirTemp(context: Context): File {
    val dir = File(context.filesDir, "project")
    if (!dir.exists()) {
        dir.mkdirs()
    }
    return dir
}