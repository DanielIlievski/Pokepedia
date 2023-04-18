package com.echo.pokepedia.ui.pokemon.home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.FragmentHomeBinding
import com.echo.pokepedia.domain.pokemon.Pokemon
import com.echo.pokepedia.domain.pokemon.PokemonList
import com.echo.pokepedia.ui.BaseFragment
import com.echo.pokepedia.util.NetworkResult
import com.echo.pokepedia.util.UiText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    // region fragment variables
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    // endregion

    // region fragment methods
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()

        initListeners()

        initOptionsMenu()

        // TODO: Just for general testing, will be removed in the next task
        viewModel.getPokemonList(20, 20)
        viewModel.getPokemonInfo("charmander")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // endregion

    private fun initObservers() {
        observePokemonList()
        observePokemonInfo()
    }

    private fun initListeners() {}

    private fun initOptionsMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_fragment_home, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.search -> {
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    // region observePokemonList
    private fun observePokemonList() = lifecycleScope.launch {
        viewModel.pokemonList.collect {result ->
            when (result) {
                is NetworkResult.Success -> onSuccessfulPokemonListFetch(result.result)
                is NetworkResult.Failure -> onFailedPokemonListFetch(result.exception)
            }
        }
    }

    private fun onSuccessfulPokemonListFetch(result: PokemonList) {
        Log.d("HomeFragment", "onSuccessfulPokemonListFetch: $result")
    }

    private fun onFailedPokemonListFetch(exception: UiText?) {
        Log.d("HomeFragment", "onFailedPokemonListFetch: ${exception!!.asString(requireContext())}")
    }
    // endregion

    // region observePokemonInfo
    private fun observePokemonInfo() = lifecycleScope.launch {
        viewModel.pokemonInfo.collect {result ->
            when (result) {
                is NetworkResult.Success -> onSuccessfulPokemonInfoFetch(result.result)
                is NetworkResult.Failure -> onFailedPokemonInfoFetch(result.exception)
            }
        }
    }

    private fun onSuccessfulPokemonInfoFetch(result: Pokemon) {
        Log.d("HomeFragment", "onSuccessfulPokemonInfoFetch: $result")
    }

    private fun onFailedPokemonInfoFetch(exception: UiText?) {
        Log.d("HomeFragment", "onFailedPokemonInfoFetch: ${exception!!.asString(requireContext())}")
    }
    // endregion

}