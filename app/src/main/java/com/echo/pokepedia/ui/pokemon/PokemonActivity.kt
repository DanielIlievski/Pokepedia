package com.echo.pokepedia.ui.pokemon

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.ActivityPokemonBinding
import com.echo.pokepedia.ui.BaseActivity
import com.echo.pokepedia.ui.BaseViewModel
import com.echo.pokepedia.ui.pokemon.home.HomeFragment
import com.echo.pokepedia.ui.pokemon.myteam.MyTeamFragment
import com.echo.pokepedia.ui.pokemon.settings.SettingsFragment
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
        binding.bottomNavigationView.selectedItemId = R.id.home

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            lateinit var selectedFragment: Fragment
            when (item.itemId) {
                R.id.pokeball -> selectedFragment = MyTeamFragment()
                R.id.home -> selectedFragment = HomeFragment()
                R.id.settings -> selectedFragment = SettingsFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_home, selectedFragment).commit()
            true
        }
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