package com.volio.draw

import android.content.Context
import android.graphics.Bitmap
import com.volio.draw.draw.DrawLayout
import com.volio.draw.model.DataDraw
import com.volio.draw.model.FrameModel
import com.volio.draw.model.ProjectModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


fun saveFrame(context: Context, projectModel: ProjectModel, frameModel: FrameModel) {
    CoroutineScope(Dispatchers.IO).launch {
        val drawLayout = DrawLayout(context) {}

        drawLayout.setData(frameModel, projectModel.background, projectModel.width, projectModel.height)
        val bitmapCache =
                Bitmap.createBitmap(projectModel.width.toInt(), projectModel.height.toInt(), Bitmap.Config.ARGB_8888)

        bitmapCache.saveBitmapToInternalStorage(
                context,
                "${frameModel.id}.png"
        )

    }

}

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

// save anh bitmap khi chuyen giua cac frame khac nhau, se có id, thoi gian chỉnh sửa.