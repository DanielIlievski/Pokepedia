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
import com.echo.pokepedia.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    // region fragment variables
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private var adapter: PokemonAdapter? = null
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

        initUI()

        initObservers()

        initListeners()

        initOptionsMenu()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // endregion

    private fun initObservers() {
        observePokemonList()
        observePokemonInfo()
        observeErrorObservable()
    }

    private fun initUI() {
        initOptionsMenu()
        setPokemonAdapter()
    }

    private fun initListeners() {
//        onRecyclerBottomReachedListener()
    }

    // region initUI
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

    private fun setPokemonAdapter() {
        adapter = PokemonAdapter {pokemon ->
            showToastMessageShort(pokemon.name)
        }
        binding.pokemonRecyclerView.adapter = adapter
    }
    // endregion

    // region initObservers

    private fun observePokemonList() = lifecycleScope.launch {
        viewModel.pokemonList.collectLatest { result ->
            adapter?.submitData(result)
        }
    }

    private fun observePokemonInfo() = lifecycleScope.launch {
        viewModel.pokemonDetailsInfo.collect { result ->
            Log.d("HomeFragment", "onSuccessfulPokemonInfoFetch: $result")
        }
    }

    private fun observeErrorObservable() = lifecycleScope.launch {
        viewModel.errorObservable.collect {exception ->
            showToastMessageLong(exception.asString(requireContext()))
            Log.d("HomeFragment", "Error: ${exception.asString(requireContext())}")
        }
    }
    // endregion

    // region initListeners
//    private fun onRecyclerBottomReachedListener() {
//        adapter?.setOnBottomReachedListener(object : OnBottomReachedListener {
//            override fun onBottomReached(position: Int) {
//                lifecycleScope.launch {
//                    viewModel.endReached.collect { endReached ->
//                        if (!endReached) {
//                            viewModel.getPokemonListPaginated()
//                        }
//                    }
//                }
//            }
//        })
//    }
    // endregion
}