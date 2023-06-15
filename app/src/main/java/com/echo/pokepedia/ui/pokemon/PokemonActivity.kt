package com.echo.pokepedia.ui.pokemon

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.ActivityPokemonBinding
import com.echo.pokepedia.ui.BaseActivity
import com.echo.pokepedia.util.appBarConfigDestinations
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PokemonActivity : BaseActivity() {

    // region activity variables
    private lateinit var binding: ActivityPokemonBinding

    private lateinit var navHostFragment: NavHostFragment

    private lateinit var navController: NavController
    // endregion

    // region activity methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokemonBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)

        initNavController()

        initBottomNavigationView()

        handleControllerOnBackPressed()
    }
    // endregion

    private fun initNavController() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_activity_pokemon) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun initBottomNavigationView() {
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

    fun hideToolbar() {
        this.supportActionBar?.hide()
    }

    fun showToolbar() {
        this.supportActionBar?.show()
    }

    private fun handleControllerOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (navController.currentDestination?.id) {
                    R.id.homeFragment -> finish()
                    R.id.myTeamFragment ->
                        binding.bottomNavigationView.selectedItemId = R.id.homeFragment
                    R.id.settingsFragment ->
                        binding.bottomNavigationView.selectedItemId = R.id.homeFragment
                    else -> navController.popBackStack()
                }
            }
        })
    }
}