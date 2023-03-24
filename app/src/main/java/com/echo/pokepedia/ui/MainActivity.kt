package com.echo.pokepedia.ui

import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

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

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}