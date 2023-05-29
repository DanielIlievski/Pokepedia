package com.echo.pokepedia.ui.pokemon.details

import android.app.ActionBar.LayoutParams
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.FragmentPokemonDetailsBinding
import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO
import com.echo.pokepedia.ui.BaseFragment
import com.echo.pokepedia.ui.pokemon.PokemonActivity
import com.echo.pokepedia.util.capitalizeFirstLetter
import com.echo.pokepedia.util.getColorRes
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT
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

        viewModel.getPokemonDetails(args.pokemonName)

        initObservers()

        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as PokemonActivity).showToolbar()
        activity?.window?.statusBarColor =
            requireContext().getColorRes(R.color.blue_pokemon_variant)
        _binding = null
    }
    // endregion

    private fun initObservers() {
        observePokemonDetails()
        observePokemonStats()
    }

    private fun initListeners() {
        onImageFavoriteClickListener()
    }

    // region init UI
    private fun initUI(pokemonDetails: PokemonDetailsDTO) {
        activity?.window?.statusBarColor = requireContext().getColorRes(R.color.black)
        initToolbar()
        setImgFavoriteSelected()
        initViews(pokemonDetails)
    }

    private fun initViews(pokemonDetails: PokemonDetailsDTO) {
        with(binding) {
            root.background = getGradientWhiteBottom(args.dominantColor)
            loadImage(pokemonDetails.imageDefault, imgPokemon)
            if (pokemonDetails.id != null && pokemonDetails.name != null) {
                pokemonNameAndId.text = requireContext().getString(
                    R.string.pokemon_id_name,
                    pokemonDetails.id,
                    pokemonDetails.name.capitalizeFirstLetter()
                )
            }
            pokemonDetails.types?.let {
                groupPokemonTypes.render(it, LinearLayout.HORIZONTAL, View.VISIBLE)
                groupPokemonTypes.visibility = View.VISIBLE
                imgFavoriteAnim.visibility = View.VISIBLE
                addToMyTeam.visibility = View.VISIBLE
            }
            setAbilitiesGroup(pokemonDetails.abilities)
        }
    }

    private fun initToolbar() {
        with(binding.toolbarPokemonDetails) {
            background = getGradientBlackTop(args.dominantColor)
            setTitleTextColor(Color.WHITE)
            elevation = 0f
            title =
                getString(
                    R.string.pokemon_id_name,
                    args.pokemonId,
                    args.pokemonName.capitalizeFirstLetter()
                )
            navigationIcon =
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_back_arrow)
            setNavigationOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
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

    private fun setImgFavoriteSelected() = lifecycleScope.launch {
        binding.imgFavoriteAnim.isSelected = viewModel.isPokemonFavorite()
    }
    // endregion

    // region initListeners

    // region onPokemonImgLongClickListener
    private fun onPokemonImgLongClickListener(pokemonDetails: PokemonDetailsDTO) {
        binding.imgPokemon.setOnLongClickListener {
            toggleDefaultOrShinyImg(pokemonDetails)
            return@setOnLongClickListener true
        }
    }

    private fun toggleDefaultOrShinyImg(pokemonDetails: PokemonDetailsDTO) {
        with(binding) {
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
        }
    }
    // endregion

    // region onImageFavoriteClickListener
    private fun onImageFavoriteClickListener() {
        with(binding) {
            imgFavoriteAnim.setOnClickListener {
                if (!imgFavoriteAnim.isSelected) {
                    showFavoritePokemonDialog()
                    imgFavoriteAnim.likeAnimation()
                }
                imgFavoriteAnim.isSelected = !imgFavoriteAnim.isSelected
            }
        }
    }

    private fun showFavoritePokemonDialog() {
        val input = TextInputEditText(requireContext())
        val inputLayout: TextInputLayout = TextInputLayout(
            requireContext(),
            null,
            R.style.TextInputLayout_OutlinedBox_Custom
        ).apply {
            hint = requireContext().getString(R.string.nickname_hint)
            layoutParams =
                LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                    .apply { setMargins(15, 5, 15, 5) }
            endIconMode = END_ICON_CLEAR_TEXT
        }
        inputLayout.addView(input)

        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setTitle(R.string.add_a_nickname_to_your_buddy)
            .setView(inputLayout)
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                lifecycleScope.launch {
                    binding.imgFavoriteAnim.isSelected = viewModel.isPokemonFavorite()
                }
                dialog.cancel()
            }
            .setCancelable(false)
            .show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            if (input.text?.isNotEmpty()!!) {
                viewModel.setBuddyPokemonName(args.pokemonName)
                viewModel.setBuddyPokemonNickname(input.text.toString())
                viewModel.setBuddyPokemonDominantColor(args.dominantColor)
                dialog.dismiss()
            } else {
                inputLayout.error = getString(R.string.enter_nickname_error)
            }
        }
    }
    // endregion

    //region onAddToMyTeamClickListener
    private fun onAddToMyTeamClickListener(pokemonDetails: PokemonDetailsDTO) {
        binding.addToMyTeam.setOnClickListener {
            lifecycleScope.launch {
                when (viewModel.checkAddConditions(pokemonDetails.id)) {
                    AddPokemonState.AlreadyExists -> showToastMessageShort(
                        getString(
                            R.string.already_exists_in_team,
                            pokemonDetails.name?.capitalizeFirstLetter()
                        )
                    )
                    AddPokemonState.TeamFull -> teamFullAlertDialog(pokemonDetails)
                    AddPokemonState.AddPokemon -> addPokemonAlertDialog(pokemonDetails)
                }
            }
        }
    }

    private fun teamFullAlertDialog(pokemonDetails: PokemonDetailsDTO) {
        showSimpleAlertDialog(
            context = requireContext(),
            title = R.string.team_full_title,
            message = R.string.team_full_message,
            positiveBtnText = R.string.yes,
            negativeBtnText = R.string.no,
            onPositiveBtnClick = { dialog, _ ->
                val isTeamFull = true
                val action =
                    PokemonDetailsFragmentDirections.pokemonDetailsFragmentToMyTeamFragment(
                        pokemonId = args.pokemonId,
                        isTeamFull = isTeamFull,
                        imgUrl = pokemonDetails.imageDefault,
                        dominantColor = args.dominantColor
                    )
                findNavController().navigate(action)
                dialog.dismiss()
            },
            onNegativeBtnClick = { dialog, _ ->
                dialog.cancel()
            }
        )
    }

    private fun addPokemonAlertDialog(pokemonDetails: PokemonDetailsDTO) {
        showSimpleAlertDialog(
            context = requireContext(),
            title = R.string.add_to_my_team_title,
            message = R.string.add_to_my_team_message,
            positiveBtnText = R.string.yes,
            negativeBtnText = R.string.no,
            onPositiveBtnClick = { dialog, _ ->
                viewModel.addPokemonToMyTeam(pokemonDetails.id)
                showToastMessageShort(
                    getString(
                        R.string.added_to_team_successfully,
                        pokemonDetails.name?.capitalizeFirstLetter()
                    )
                )
                dialog.dismiss()
            },
            onNegativeBtnClick = { dialog, _ ->
                dialog.cancel()
            })
    }
    //endregion
    // endregion

    // region initObservers

    // region observePokemonDetails
    private fun observePokemonDetails() = lifecycleScope.launch {
        viewModel.pokemonDetailsInfo.collect { pokemonDetails ->
            initUI(pokemonDetails)
            onPokemonImgLongClickListener(pokemonDetails)
            onAddToMyTeamClickListener(pokemonDetails)
        }
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