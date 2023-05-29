package com.echo.pokepedia.ui.pokemon.myteam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.FragmentMyTeamBinding
import com.echo.pokepedia.ui.BaseFragment
import com.echo.pokepedia.ui.pokemon.PokemonActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyTeamFragment : BaseFragment() {

    // region fragment variables
    private var _binding: FragmentMyTeamBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyTeamViewModel by viewModels()

    private lateinit var adapter: MyTeamAdapter

    private val args by navArgs<MyTeamFragmentArgs>()

    private var isTeamFull: Boolean = false
    // endregion

    // region fragment methods
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyTeamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initTeam()

        isTeamFull = args.isTeamFull

        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // endregion

    private fun initObservers() {
        observeMyTeamList()
    }

    // region initObservers

    // region observeMyTeamList
    private fun observeMyTeamList() = lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.myTeamList.collectLatest { teamList ->
                val height = calculateRecyclerViewHeight()
                adapter = MyTeamAdapter(
                    myTeamList = teamList.toMutableList(),
                    isTeamFull = isTeamFull,
                    height = height
                ) { pokemon ->
                    pokemon.id?.let { alertDialogDeleteOnPosition(it) }
                }
                binding.myTeamRecyclerView.adapter = adapter
            }
        }
    }

    private fun calculateRecyclerViewHeight(): Int {
        val navHostHeight =
            requireActivity().findViewById<FragmentContainerView>(R.id.nav_host_activity_pokemon).height
        val actionBarHeight = (requireActivity() as PokemonActivity).supportActionBar?.height ?: 0
        return navHostHeight - actionBarHeight
    }
    // endregion
    // endregion

    private fun alertDialogDeleteOnPosition(pokemonId: Int) {
        showSimpleAlertDialog(
            context = requireContext(),
            title = R.string.remove_from_my_team_title,
            message = R.string.remove_from_my_team_message,
            positiveBtnText = R.string.yes,
            negativeBtnText = R.string.no,
            onPositiveBtnClick = { dialog, _ ->
                viewModel.removeFromMyTeam(pokemonId)
                if (isTeamFull) {
                    isTeamFull = false
                    viewModel.addPokemonToMyTeam(pokemonId = args.pokemonId)
                }
                dialog.dismiss()
            },
            onNegativeBtnClick = { dialog, _ ->
                dialog.cancel()
            }
        )
    }
}