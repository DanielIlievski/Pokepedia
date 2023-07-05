package com.echo.pokepedia.ui.splash

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.ActivitySplashBinding
import com.echo.pokepedia.ui.BaseActivity
import com.echo.pokepedia.ui.MainActivity
import com.echo.pokepedia.ui.authentication.AuthActivity
import com.echo.pokepedia.util.getColorRes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    // region activity variables
    private lateinit var binding: ActivitySplashBinding

    private val viewModel: SplashViewModel by viewModels()
    // endregion

    // region acivity methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.window?.statusBarColor = this.getColorRes(R.color.charcoal)

        loopSplashAnimation()

        postDelay(2000) {
            lifecycleScope.launch {
                viewModel.isOnBoardingAvailable.collect { isOnBoardingAvailable ->
                    val intent = if (isOnBoardingAvailable)
                        Intent(this@SplashActivity, MainActivity::class.java)
                    else
                        Intent(this@SplashActivity, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.window?.statusBarColor = this.getColorRes(R.color.blue_pokemon_variant)
    }
    // endregion

    private fun loopSplashAnimation() {
        val animated = AnimatedVectorDrawableCompat.create(this, R.drawable.avd_splash_screen_logo)
        animated?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                lifecycleScope.launch {
                    delay(200)
                    binding.imgSplashAnim.post { animated.start() }
                }
            }
        })
        binding.imgSplashAnim.setImageDrawable(animated)
        animated?.start()
    }
}