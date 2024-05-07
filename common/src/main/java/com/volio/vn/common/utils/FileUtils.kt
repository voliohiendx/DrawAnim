package com.volio.vn.common.utils

import android.app.PendingIntent
import android.content.*
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import java.io.File
import java.net.URLDecoder

fun Context.rename(
    originUri: Uri,
    originPath: String,
    destPath: String,
    onRenameFailed: (File) -> Unit,
    onRenameSuccess: (newPath: String, newUri: Uri, validName: String) -> Unit
) {
    val parentDestFolder = destPath.substringBeforeLast("/")
    val originFile = File(originPath)
    val destFile = File(destPath)
    val dirDestFolder = File(parentDestFolder)

    var validName: String = destFile.absolutePath.substringAfterLast("/")
    var founding = true
    var index = 1

    val filesInFolder = dirDestFolder.listFiles()
    if (filesInFolder != null) {
        while (founding) {
            val founded =
                filesInFolder.find { it.absolutePath.substringAfterLast("/") == validName }
            if (founded == null) {
                founding = false
            } else {
                val name = validName.substringBeforeLast(".")
                val extension = validName.substringAfterLast(".")
                validName = name + "-$index" + "." + extension
                index++
            }
        }
    }

    val fileToSave = File(parentDestFolder, validName)

    when {
        Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q -> {
            if (originFile.renameTo(fileToSave)) {
                MediaScannerConnection.scanFile(
                    this,
                    arrayOf(fileToSave.absolutePath, originFile.absolutePath),
                    null
                ) { path, uri ->
                    if (path != null && uri != null && path == fileToSave.absolutePath) {
                        onRenameSuccess.invoke(path, uri, validName)
                    } else {
                        onRenameFailed.invoke(fileToSave)
                    }
                }
            } else {
                onRenameFailed.invoke(fileToSave)
            }
        }
        else -> {
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, validName)
            contentResolver.update(originUri, contentValues, null, null)
            MediaScannerConnection.scanFile(
                this,
                arrayOf(File(originPath).absolutePath, fileToSave.absolutePath),
                null
            ) { path, uri ->
                if (path != null && uri != null && path == fileToSave.absolutePath) {
                    onRenameSuccess.invoke(path, uri, validName)
                } else {
                    onRenameFailed.invoke(fileToSave)
                }
            }
        }
    }
}

fun Context.delete(
    launcher: ActivityResultLauncher<IntentSenderRequest>,
    fileUri: Uri,
    filePath: String,
    onFailed: () -> Unit,
    onSuccess: () -> Unit
) {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
        try {
            if (File(filePath).delete()) {
                MediaScannerConnection.scanFile(
                    this, arrayOf(filePath),
                    null
                ) { path, uri ->
                    if (uri == null && path == filePath) {
                        onSuccess.invoke()
                    } else {
                        onFailed.invoke()
                    }
                }
            } else {
                onFailed.invoke()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            onFailed.invoke()
        }
    } else {
        val mediaID = fileUri.toString().substringAfterLast("/")
        val uriDelete: Uri = ContentUris.withAppendedId(
            MediaStore.Video.Media.getContentUri("external"),
            mediaID.toLong()
        )

        try {
            contentResolver.delete(uriDelete, null, null)
            MediaScannerConnection.scanFile(
                this, arrayOf(filePath),
                null
            ) { path, uri ->
                if (uri == null && path == filePath) {
                    onSuccess.invoke()
                } else {
                    onFailed.invoke()
                }
            }
        } catch (ex: Exception) {
            var pendingIntent: PendingIntent? = null
            val collection: ArrayList<Uri> = ArrayList()
            collection.add(uriDelete)
            pendingIntent = MediaStore.createDeleteRequest(contentResolver, collection)

            val sender: IntentSender = pendingIntent.intentSender
            val request: IntentSenderRequest = IntentSenderRequest.Builder(sender).build()
            launcher.launch(request)
        }
    }
}

fun delete2(context: Context, file: File): Boolean {
    val where = MediaStore.MediaColumns.DATA + "=?"
    val selectionArgs = arrayOf(
        file.absolutePath
    )
    val contentResolver = context.contentResolver
    val filesUri = MediaStore.Files.getContentUri("external")
    contentResolver.delete(filesUri, where, selectionArgs)
    if (file.exists()) {
        contentResolver.delete(filesUri, where, selectionArgs)
        MediaScannerConnection.scanFile(
            context, arrayOf(file.absolutePath),
            null
        ) { path, uri ->

        }
    }
    return !file.exists()
}

fun delete11(context: Context, uri: Uri) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        MediaStore.createDeleteRequest(context.contentResolver, arrayListOf(uri))
    }
}

fun exportMp4ToGallery(context: Context?, filePath: String?, width: Int, height: Int) {
    val values = ContentValues()
    values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
    values.put(MediaStore.Video.Media.DATA, filePath)
    values.put(MediaStore.Video.Media.WIDTH, width)
    values.put(MediaStore.Video.Media.HEIGHT, height)
    context?.contentResolver?.insert(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        values
    )
    MediaScannerConnection.scanFile(
        context, arrayOf(filePath),
        null, null
    )

}

fun exportFileToGallery(context: Context?, filePath: String?) {
    MediaScannerConnection.scanFile(
        context, arrayOf(filePath),
        null, null
    )
}


fun Uri.toAudioMediaUri(): Uri {
    var mediaID = toString().substringAfterLast("/")
    mediaID = URLDecoder.decode(mediaID, "UTF-8")
    return try {
        if (mediaID.contains(":")) {
            mediaID = mediaID.substringAfterLast(":")
        }

        ContentUris.withAppendedId(
            MediaStore.Audio.Media.getContentUri("external"),
            mediaID.toLong()
        )
    } catch (e: Exception) {
        e.printStackTrace()
        this
    }
}

fun Uri.toFileUri(): Uri {
    val mediaID = toString().substringAfterLast("/")
    return ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), mediaID.toLong())
}