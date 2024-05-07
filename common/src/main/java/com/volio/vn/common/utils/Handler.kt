package com.volio.vn.common.utils

import android.os.Handler
import android.os.Looper
import android.view.View

fun Any.delay(durationInMillis: Long, block: () -> Unit) {
    Handler(Looper.getMainLooper())
        .postDelayed({ block.invoke() }, durationInMillis)
}

