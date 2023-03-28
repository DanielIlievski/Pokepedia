package com.echo.pokepedia.ui

import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    fun postDelay(duration: Long = 1000, callBack: () -> Unit) {
        Handler(Looper.getMainLooper())
            .postDelayed({
                callBack.invoke()
            }, duration)
    }
}