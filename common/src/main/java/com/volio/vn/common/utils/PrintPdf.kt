package com.volio.vn.common.utils

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.*
import java.io.*

object PrintPdf {

    fun printPDF(activity: Activity, filePdf: File): PrintJob {
        val printAttributes = PrintAttributes.Builder()
        printAttributes.setMediaSize(PrintAttributes.MediaSize.ISO_A4)
        printAttributes.setMinMargins(PrintAttributes.Margins.NO_MARGINS)
        return printPDF(
            activity,
            filePdf,
            printAttributes.build()
        )

    }
    private fun printPDF(
        activity: Activity,
        pdfFileToPrint: File,
        printAttributes: PrintAttributes?
    ): PrintJob {
        val printManager = activity.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = java.lang.Long.valueOf(System.currentTimeMillis()).toString()
        return printManager.print(jobName, object : PrintDocumentAdapter() {
            override fun onWrite(
                pages: Array<PageRange>,
                destination: ParcelFileDescriptor,
                cancellationSignal: CancellationSignal,
                callback: WriteResultCallback
            ) {
                var input: InputStream? = null
                var output: OutputStream? = null
                try {
                    input = FileInputStream(pdfFileToPrint)
                    output = FileOutputStream(destination.fileDescriptor)
                    val buf = ByteArray(1024)
                    var bytesRead: Int
                    while (input.read(buf).also { bytesRead = it } > 0) {
                        output.write(buf, 0, bytesRead)
                    }
                    callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        input!!.close()
                        output!!.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onLayout(
                oldAttributes: PrintAttributes,
                newAttributes: PrintAttributes,
                cancellationSignal: CancellationSignal,
                callback: LayoutResultCallback,
                extras: Bundle
            ) {
                if (cancellationSignal.isCanceled) {
                    callback.onLayoutCancelled()
                    return
                }
                val pdi = PrintDocumentInfo.Builder(pdfFileToPrint.name).setContentType(
                    PrintDocumentInfo.CONTENT_TYPE_DOCUMENT
                ).build()
                callback.onLayoutFinished(pdi, true)
            }
        }, printAttributes)
    }
}
