package com.echo.pokepedia.ui.authentication

import android.os.Bundle
import com.echo.pokepedia.R
import com.echo.pokepedia.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}