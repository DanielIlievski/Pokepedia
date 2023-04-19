package com.echo.pokepedia.ui.home

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.ActivityHomeScreenBinding
import com.echo.pokepedia.ui.BaseActivity
import com.echo.pokepedia.util.appBarConfigDestinations
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PokemonActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavigationView()
    }

    private fun initBottomNavigationView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_home) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        val configuration = AppBarConfiguration(appBarConfigDestinations)
        setupActionBarWithNavController(navController, configuration)
    }
}