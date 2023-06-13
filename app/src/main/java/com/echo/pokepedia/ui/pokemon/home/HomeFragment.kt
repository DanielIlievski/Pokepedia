package com.echo.pokepedia.ui.pokemon.home

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
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
import com.echo.pokepedia.util.onQueryTextChanged
import com.echo.pokepedia.util.viewHideOnExpandShowOnCollapse
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

    private var queriedListAdapter: QueriedPokemonListAdapter? = null
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

        viewModel.getPokemonListPaginated()
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
        setQueriedListAdapter()
    }

    private fun initObservers() {
        observePokemonList()
        observeQueriedPokemonList()
        observeBuddyPokemonNickname()
        observeBuddyPokemonDetails()
        observeBuddyPokemonDominantColor()
        observeHomeViewState()
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

                val searchItem = menu.findItem(R.id.search)
                val searchView = searchItem.actionView as SearchView
                searchItem.viewHideOnExpandShowOnCollapse(binding.buddyPokemonSection.root)
                searchView.onQueryTextChanged { query ->
                    viewModel.searchPokemonList(query)
                }
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

    private fun setQueriedListAdapter() {
        queriedListAdapter = QueriedPokemonListAdapter { pokemon ->
            val action = HomeFragmentDirections.homeFragmentToPokemonDetailsFragment(
                pokemon.id ?: -1,
                pokemon.name.orEmpty(),
                pokemon.dominantColor ?: Color.WHITE,
                pokemon.dominantColorShiny ?: Color.WHITE
            )

            findNavController().navigate(action)
        }
        binding.queriedPokemonListRecyclerView.adapter = queriedListAdapter
    }
    // endregion

    // region initObservers
    private fun observePokemonList() = lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.pokemonList.collectLatest { result ->
                adapter?.submitData(result)
                val currentState = viewModel.homeViewState.value
                if (currentState is HomeViewState.ShowPokemonListPaginated && adapter?.itemCount == 0) {
                    binding.textEmptyState.visibility = View.VISIBLE
                    binding.textEmptyState.text = getString(R.string.empty_pokemon_list)
                } else if (currentState is HomeViewState.ShowPokemonListPaginated) {
                    binding.textEmptyState.visibility = View.GONE
                }
            }
        }
    }

    private fun observeQueriedPokemonList() = lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.queriedPokemonList.collectLatest { result ->
                queriedListAdapter?.submitList(result)
                binding.queriedPokemonListRecyclerView.scrollToPosition(0)
                val currentState = viewModel.homeViewState.value
                if (currentState is HomeViewState.ShowQueriedPokemonList && result.isEmpty()) {
                    binding.textEmptyState.visibility = View.VISIBLE
                    binding.textEmptyState.text = getString(R.string.no_such_pokemon)
                } else if (currentState is HomeViewState.ShowQueriedPokemonList) {
                    binding.textEmptyState.visibility = View.GONE
                }
            }
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
                        loadImage(pokemon.imageDefault, imgPokemon)
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

    private fun observeHomeViewState() = lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.homeViewState.collectLatest { viewState ->
                when (viewState) {
                    HomeViewState.ShowPokemonListPaginated -> {
                        binding.pokemonRecyclerView.visibility = View.VISIBLE
                        binding.queriedPokemonListRecyclerView.visibility = View.GONE
                    }
                    HomeViewState.ShowQueriedPokemonList -> {
                        binding.pokemonRecyclerView.visibility = View.GONE
                        binding.queriedPokemonListRecyclerView.visibility = View.VISIBLE
                    }
                    else -> {}
                }
            }
        }
    }
    // endregion

    // region initListeners
    private fun onFabClickListener() {
        binding.fabScrollToTop.setOnClickListener {
            binding.pokemonRecyclerView.smoothScrollToPosition(0)
            binding.queriedPokemonListRecyclerView.smoothScrollToPosition(0)
        }
    }

    private fun onRecyclerScrollListener() {
        binding.pokemonRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                binding.fabScrollToTop.visibility = if (dy >= 0) View.GONE else View.VISIBLE
            }
        })
        binding.queriedPokemonListRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                binding.fabScrollToTop.visibility = if (dy >= 0) View.GONE else View.VISIBLE
            }
        })
    }
    // endregion
}