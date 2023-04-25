package com.echo.pokepedia.ui.pokemon

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.ActivityPokemonBinding
import com.echo.pokepedia.ui.BaseActivity
import com.echo.pokepedia.util.appBarConfigDestinations
import com.echo.pokepedia.ui.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PokemonActivity : BaseActivity() {

    private lateinit var binding: ActivityPokemonBinding

    private val viewModel: BaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokemonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavigationView()

        initObservers()
    }

    private fun initBottomNavigationView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_home) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        val configuration = AppBarConfiguration(appBarConfigDestinations)
        setupActionBarWithNavController(navController, configuration)
    }

    private fun initObservers() {
        observeErrorObservable()
    }

    private fun observeErrorObservable() = lifecycleScope.launch {
        viewModel.errorObservable.collect { exceptionMessage ->
            val msg = exceptionMessage.asString(this@PokemonActivity)
            if (msg.isNotEmpty()) {
                showToastMessage(exceptionMessage.asString(this@PokemonActivity), Toast.LENGTH_LONG)
            }
        }
    }
}