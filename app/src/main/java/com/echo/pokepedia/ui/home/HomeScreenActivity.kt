package com.echo.pokepedia.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.ActivityHomeScreenBinding
import com.echo.pokepedia.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeScreenActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavigationView()
    }

    private fun initBottomNavigationView() {
        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_home, HomeFragment()).commit()
        binding.bottomNavigationView.selectedItemId = R.id.home

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            lateinit var selectedFragment: Fragment
            when (item.itemId) {
                R.id.pokeball -> selectedFragment = MyTeamFragment()
                R.id.home -> selectedFragment = HomeFragment()
                R.id.settings -> selectedFragment = SettingsFragment()
            }

            supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_home, selectedFragment).commit()
            true
        }
    }
}