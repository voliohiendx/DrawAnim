package com.volio.vn.common.utils

import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner


fun Fragment.isFragmentResumed(block: () -> Unit) {
    if (lifecycle.currentState == Lifecycle.State.RESUMED) {
        block.invoke()
    }
}

fun Fragment.setBackPressListener(viewBack: View? = null, onClickBack: () -> Unit) {
    viewBack?.setPreventDoubleClick {
        onClickBack()
    }
    activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, true) {
        onClickBack()
    }
}

fun Fragment.safeCall(onCall: () -> Unit) {
    Handler(Looper.getMainLooper()).post {
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_RESUME) {
                    onCall.invoke()
                    lifecycle.removeObserver(this)
                }
                if (event == Lifecycle.Event.ON_DESTROY) {
                    lifecycle.removeObserver(this)
                }
            }
        })
    }
}

fun FragmentActivity.transparentStatusAndNavigation() {
    runCatching {
        var systemUiVisibility = 0
        val winParams = window.attributes
        val statusBarColor: Int = Color.TRANSPARENT
        val navigationBarColor: Int = Color.argb(1,0,0,0)

        systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        systemUiVisibility = systemUiVisibility or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        window.decorView.systemUiVisibility = systemUiVisibility
        winParams.flags = winParams.flags and
                (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION).inv()
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor

        window.attributes = winParams
        setSystemUIVisibility(true)
    }

}

fun FragmentActivity.defaultStatusBarNavigation() {
    window.decorView.systemUiVisibility = 0
    val winParams = window.attributes
    window.statusBarColor = Color.BLACK
    window.navigationBarColor = Color.BLACK
    window.attributes = winParams
    setSystemUIVisibility(false)

}
fun FragmentActivity.setSystemUIVisibility(hide: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val window = window.insetsController!!
//        val windows = WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
        val windows =  WindowInsets.Type.navigationBars()
        if (hide) window.hide(windows) else window.show(windows)
        // needed for hide, doesn't do anything in show
        window.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}
