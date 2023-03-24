package com.echo.pokepedia.ui

import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import com.echo.pokepedia.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private var keepSplashScreenOn = true

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().setKeepOnScreenCondition { keepSplashScreenOn }
        installSplashScreen().setOnExitAnimationListener { splashScreenViewProvider ->
            val anim = AnimatorInflater.loadAnimator(this, R.anim.fade_out)
            anim.setTarget(splashScreenViewProvider.view)
            anim.start()
        }

        Handler(Looper.getMainLooper())
            .postDelayed({
                keepSplashScreenOn = false

            }, 1000)

        setContentView(R.layout.activity_main)
    }
}