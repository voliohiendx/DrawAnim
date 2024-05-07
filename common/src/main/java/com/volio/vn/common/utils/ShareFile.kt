package com.volio.vn.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import java.io.File

object ShareFile {
    fun sharePdfFile(context: Context, idApp: String, pdfPath: String) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${idApp}.provider",
            File(pdfPath)
        )
        val intent: Intent = ShareCompat.IntentBuilder(context)
            .setType("application/pdf")
            .setStream(uri)
            .createChooserIntent()
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)

    }

    fun shareText(context: Context, text: String) {
        val intent: Intent = ShareCompat.IntentBuilder(context)
                .setType("text/plain")
                .setText(text)
                .createChooserIntent()
        context.startActivity(intent)
    }

    fun sharePdfFile(context: Context, idApp: String, listFile: List<File>) {
        val shareCompat: ShareCompat.IntentBuilder = ShareCompat.IntentBuilder(context)
        listFile.forEach { file ->
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${idApp}.provider",
                File(file.path)
            )
            shareCompat.addStream(uri)
        }
        val intent: Intent = shareCompat.setType("application/pdf")
            .createChooserIntent()
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        context.startActivity(intent)

    }
}