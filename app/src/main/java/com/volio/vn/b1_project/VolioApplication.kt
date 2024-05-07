package com.volio.vn.b1_project

import android.app.Application
import com.tencent.mmkv.MMKV
import com.volio.vn.b1_project.ui.MainActivity
import com.zxy.recovery.callback.RecoveryCallback
import com.zxy.recovery.core.Recovery
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class VolioApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Init MMKV - Key_value storage like SharePreferences
        // https://github.com/Tencent/MMKV/wiki/android_tutorial
        MMKV.initialize(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Recovery.getInstance()
            .debug(BuildConfig.DEBUG)
            .recoverInBackground(false)
            .recoverStack(true)
            .mainPage(MainActivity::class.java)
            .recoverEnabled(true)
            .callback(object : RecoveryCallback {
                override fun stackTrace(stackTrace: String?) {
                    Timber.e(stackTrace)
                }

                override fun cause(cause: String?) {
                    Timber.e(cause)
                }

                override fun exception(
                    throwExceptionType: String?,
                    throwClassName: String?,
                    throwMethodName: String?,
                    throwLineNumber: Int
                ) {
                    Timber.e(throwExceptionType, throwClassName, throwMethodName, throwLineNumber)
                }

                override fun throwable(throwable: Throwable?) {
                    Timber.e(throwable)
                }
            })
            .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
            .init(this)
    }
}
