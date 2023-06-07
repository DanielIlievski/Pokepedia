package com.echo.pokepedia.ui.pokemon.home

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.FragmentHomeBinding
import com.echo.pokepedia.ui.BaseFragment
import com.echo.pokepedia.util.capitalizeFirstLetter
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

        viewModel.getPokemonInfo()

        initUI()

        initObservers()

        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.clearBuddyPokemonDetails()
    }
    // endregion

    private fun initUI() {
        initOptionsMenu()
        setPokemonAdapter()
    }

    private fun initObservers() {
        observePokemonList()
        observeBuddyPokemonNickname()
        observeBuddyPokemonDetails()
        observeBuddyPokemonDominantColor()
    }

    private fun initListeners() {
        onFabClickListener()
        onRecyclerScrollListener()
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
        adapter = PokemonAdapter { pokemon ->
            val action = HomeFragmentDirections.homeFragmentToPokemonDetailsFragment(
                pokemon.id ?: -1,
                pokemon.name.orEmpty(),
                pokemon.dominantColor ?: Color.WHITE,
                pokemon.dominantColorShiny ?: Color.WHITE
            )

            findNavController().navigate(action)
        }
        adapter.let { pokemonAdapter ->
            val footerAdapter = PokemonLoadStateAdapter { pokemonAdapter?.retry() }
            binding.pokemonRecyclerView.adapter = pokemonAdapter?.withLoadStateFooter(footerAdapter)
            binding.pokemonRecyclerView.layoutManager =
                GridLayoutManager(requireContext(), 2).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (position == pokemonAdapter?.itemCount && footerAdapter.itemCount > 0) {
                                2
                            } else {
                                1
                            }
                        }
                    }
                }
        }
    }
    // endregion

    // region initObservers
    private fun observePokemonList() = lifecycleScope.launch {
        viewModel.pokemonList.collectLatest { result ->
            adapter?.submitData(result)
        }
    }

    private fun observeBuddyPokemonNickname() = lifecycleScope.launch {
        viewModel.buddyPokemonNickname.observe(viewLifecycleOwner) { pokemonNickname ->
            if (pokemonNickname.isNotEmpty()) {
                binding.buddyPokemonSection.textPokemonNickname.text = pokemonNickname
            }
        }
    }

    private fun observeBuddyPokemonDetails() = lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.buddyPokemonDetails.collect { pokemon ->
                if (pokemon != null) {
                    with(binding.buddyPokemonSection) {
                        loadImage(pokemon.imageDefault, imgPokemon, R.drawable.progress_spinner_anim)
                        textPokemonName.text = pokemon.name?.capitalizeFirstLetter()
                        pokemon.types?.let {
                            groupPokemonTypes.render(
                                it,
                                LinearLayout.VERTICAL,
                                View.VISIBLE
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observeBuddyPokemonDominantColor() = lifecycleScope.launch {
        viewModel.buddyPokemonDominantColor.observe(viewLifecycleOwner) { dominantColor ->
            if (dominantColor != Color.WHITE) {
                binding.buddyPokemonSection.buddyPokemonContainer.background =
                    getGradientBLTRBottomRounded(dominantColor, Color.WHITE)
            }
        }
    }
    // endregion

    // region initListeners
    private fun onFabClickListener() {
        binding.fabScrollToTop.setOnClickListener {
            binding.pokemonRecyclerView.smoothScrollToPosition(0)
        }
    }

    private fun onRecyclerScrollListener() {
        binding.pokemonRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                binding.fabScrollToTop.visibility = if (dy >= 0) View.GONE else View.VISIBLE
            }
        })
    }
    // endregion
}