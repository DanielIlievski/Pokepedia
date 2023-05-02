package com.echo.pokepedia.ui.pokemon.details

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.FragmentPokemonDetailsBinding
import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO
import com.echo.pokepedia.ui.BaseFragment
import com.echo.pokepedia.ui.pokemon.PokemonActivity
import com.echo.pokepedia.util.capitalizeFirstLetter
import com.echo.pokepedia.util.getColorRes
import com.echo.pokepedia.util.parseTypeToColorRes
import com.echo.pokepedia.util.parseTypeToDrawableRes
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PokemonDetailsFragment : BaseFragment() {

    // region fragment variables
    private var _binding: FragmentPokemonDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PokemonDetailsViewModel by viewModels()

    private val args by navArgs<PokemonDetailsFragmentArgs>()

    private var adapter = PokemonBaseStatsAdapter(emptyList())

    private var isDefaultImg = true

    private var chipList = mutableListOf<Chip>()
    // endregion

    // region fragment methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as PokemonActivity).hideToolbar()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()

        viewModel.getPokemonDetails(args.pokemonName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as PokemonActivity).showToolbar()
        _binding = null
    }
    // endregion

    private fun initUI(pokemonDetails: PokemonDetailsDTO) {
        initViews(pokemonDetails)
        initToolbar()
    }

    private fun initObservers() {
        observePokemonDetails()
        observePokemonStats()
    }

    // region init UI
    private fun initViews(pokemonDetails: PokemonDetailsDTO) {
        activity?.actionBar?.title = getString(R.string.pokemon_id_name, 1, args.pokemonName)
        binding.root.background = getGradientWhiteBottom(args.dominantColor)
        loadImage(pokemonDetails.imageDefault, binding.imgPokemon)
        if (pokemonDetails.id != null && pokemonDetails.name != null) {
            binding.pokemonNameAndId.text = requireContext().getString(
                R.string.pokemon_id_name,
                pokemonDetails.id,
                pokemonDetails.name.capitalizeFirstLetter()
            )
        }
        setTypesGroup(pokemonDetails.types)
        if (pokemonDetails.abilities != null) {
            setAbilitiesGroup(pokemonDetails.abilities)
        }
    }

    private fun initToolbar() {
        with(binding) {
            toolbarPokemonDetails.background = getGradientBlackTop(args.dominantColor)
            toolbarPokemonDetails.setTitleTextColor(Color.WHITE)
            toolbarPokemonDetails.elevation = 0f
            toolbarPokemonDetails.title =
                getString(R.string.pokemon_id_name, args.pokemonId, args.pokemonName.capitalizeFirstLetter())
            toolbarPokemonDetails.navigationIcon =
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_back_arrow)
            toolbarPokemonDetails.setNavigationOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }
    // endregion

    // region initListeners
    private fun onPokemonImgLongClickListener(pokemonDetails: PokemonDetailsDTO) {
        with(binding) {
            imgPokemon.setOnLongClickListener {
                val imgUrl =
                    if (isDefaultImg) pokemonDetails.imageShiny else pokemonDetails.imageDefault
                val color = if (isDefaultImg) args.dominantColorShiny else args.dominantColor

                loadImage(imgUrl, imgPokemon)
                root.background = getGradientWhiteBottom(color)
                viewModel.refreshStats()

                toolbarPokemonDetails.background = getGradientBlackTop(
                    if (isDefaultImg) args.dominantColorShiny else args.dominantColor
                )

                isDefaultImg = !isDefaultImg
                return@setOnLongClickListener true
            }
        }
    }
    // endregion

    // region initObservers

    // region observePokemonDetails
    private fun observePokemonDetails() = lifecycleScope.launch {
        viewModel.pokemonDetailsInfo.collect { pokemonDetails ->
            initUI(pokemonDetails)
            onPokemonImgLongClickListener(pokemonDetails)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun setTypesGroup(types: List<String>?) {
        types?.forEach { type ->
            MaterialButton(requireContext()).apply {
                id = View.generateViewId()
                text = type.capitalizeFirstLetter()
                setTextColor(requireContext().getColorRes(R.color.black))
                setAutoSizeTextTypeUniformWithConfiguration(12, 16, 1, TypedValue.COMPLEX_UNIT_SP)
                maxLines = 1
                layoutParams = LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
                    .apply {
                        setMargins(10, 5, 10, 5)
                    }
                setBackgroundColor(requireContext().getColorRes(parseTypeToColorRes(type)))
                icon =
                    AppCompatResources.getDrawable(requireContext(), parseTypeToDrawableRes(type))
                iconTintMode = PorterDuff.Mode.MULTIPLY
                iconSize = resources.getDimension(R.dimen.size_xxlarge).toInt()
                iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
                isClickable = false
                isFocusable = false
                isAllCaps = false
                cornerRadius = resources.getDimension(R.dimen.radius_xlarge).toInt()
                binding.groupPokemonTypes.addView(this)
            }
        }
    }

    private fun setAbilitiesGroup(abilities: List<String>?) {
        abilities?.forEach { ability ->
            Chip(requireContext()).apply {
                id = View.generateViewId()
                text = ability
                layoutParams =
                    LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                gravity = Gravity.CENTER
                isSingleLine = true
                isClickable = false
                isFocusable = false
                chipBackgroundColor = ColorStateList.valueOf(args.dominantColor)
                binding.chipGroupPokemonAbilities.addView(this)
                chipList.add(this)
            }
        }
        binding.horizontalScroll.clipToOutline = true
    }
    // endregion

    // region observePokemonStats
    private fun observePokemonStats() = lifecycleScope.launch {
        viewModel.pokemonStats.collect { pokemonStats ->
            adapter = PokemonBaseStatsAdapter(pokemonStats!!)
            binding.baseStatsRecycler.adapter = adapter
        }
    }
    // endregion
    // endregion
}