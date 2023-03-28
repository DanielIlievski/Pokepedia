package com.echo.pokepedia.ui

import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private var keepSplashScreenOn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        initSplashScreen()

        setContentView(binding.root)
    }

    @SuppressLint("ResourceType")
    private fun initSplashScreen() {
        installSplashScreen().setKeepOnScreenCondition { keepSplashScreenOn }
        installSplashScreen().setOnExitAnimationListener { splashScreenViewProvider ->
            val anim = AnimatorInflater.loadAnimator(this, R.anim.fade_out)
            anim.setTarget(splashScreenViewProvider.view)
            anim.start()
        }

        postDelay {
            keepSplashScreenOn = false
        }
    }
}