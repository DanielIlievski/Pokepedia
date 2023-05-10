package com.echo.pokepedia.ui.pokemon

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.ActivityPokemonBinding
import com.echo.pokepedia.ui.BaseActivity
import com.echo.pokepedia.ui.BaseViewModel
import com.echo.pokepedia.util.appBarConfigDestinations
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PokemonActivity : BaseActivity() {

    // region activity variables
    private lateinit var binding: ActivityPokemonBinding

    private val viewModel: BaseViewModel by viewModels()
    // endregion

    // region activity methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokemonBinding.inflate(layoutInflater)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(binding.root)

        initBottomNavigationView()

        observeErrorObservable()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
    // endregion

    private fun initBottomNavigationView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_activity_pokemon) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        showHideNavigation(navController)

        val configuration = AppBarConfiguration(appBarConfigDestinations)
        setupActionBarWithNavController(navController, configuration)
    }

    private fun showHideNavigation(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in appBarConfigDestinations) {
                binding.bottomNavigationView.visibility = View.VISIBLE
            } else {
                binding.bottomNavigationView.visibility = View.GONE
            }
        }
    }

    @SuppressLint("RestrictedApi")
    fun hideToolbar() {
        this.supportActionBar?.hide()
        this.supportActionBar?.setShowHideAnimationEnabled(false)
    }

    @SuppressLint("RestrictedApi")
    fun showToolbar() {
        this.supportActionBar?.show()
        supportActionBar?.setShowHideAnimationEnabled(true)
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