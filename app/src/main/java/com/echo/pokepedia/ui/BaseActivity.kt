package com.echo.pokepedia.ui

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    fun postDelay(duration: Long = 1000, callBack: () -> Unit) {
        Handler(Looper.getMainLooper())
            .postDelayed({
                callBack.invoke()
            }, duration)
    }

    fun showToastMessage(message: String?, duration: Int) {
        Toast.makeText(this, message, duration).show()
    }
}