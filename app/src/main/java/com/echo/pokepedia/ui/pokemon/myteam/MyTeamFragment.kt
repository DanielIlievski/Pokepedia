package com.echo.pokepedia.ui.pokemon.myteam

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.FragmentMyTeamBinding
import com.echo.pokepedia.ui.BaseFragment
import com.echo.pokepedia.util.getColorRes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyTeamFragment : BaseFragment() {

    // region fragment variables
    private var _binding: FragmentMyTeamBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyTeamViewModel by viewModels()

    private val myImageViewList = mutableListOf<ImageView>()

    private val myBtnDeleteList = mutableListOf<ImageButton>()
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
        initImageViewList()

        initBtnDeleteList()

        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // endregion

    private fun initImageViewList() = with(binding) {
        myImageViewList.addAll(
            listOf(imgPokemon1, imgPokemon2, imgPokemon3, imgPokemon4, imgPokemon5, imgPokemon6)
        )
    }

    private fun initBtnDeleteList() = with(binding) {
        myBtnDeleteList.addAll(
            listOf(btnDelete1, btnDelete2, btnDelete3, btnDelete4, btnDelete5, btnDelete6)
        )
    }

    private fun initObservers() {
        observeMyTeamList()
    }

    // region initObservers
    private fun observeMyTeamList() = lifecycleScope.launch {
        viewModel.myTeamList.asLiveData().observe(viewLifecycleOwner) { teamList ->
            Log.d("HelloWorld444", "observeMyTeamList: $teamList")
            initListeners(teamList)
            myBtnDeleteList.forEachIndexed { i, _ ->
                if (i < teamList.size) {
                    loadImage(teamList[i].first, myImageViewList[i])
                    myImageViewList[i].background = getGradientWhiteBottom(teamList[i].second)
                    myBtnDeleteList[i].visibility = View.VISIBLE
                } else {
                    myBtnDeleteList[i].visibility = View.GONE
                    myImageViewList[i].backgroundTintList =
                        ColorStateList.valueOf(requireContext().getColorRes(R.color.grey_light))
                    myImageViewList[i].setImageResource(R.drawable.ic_add_pokemon)

                }

            }
        }
    }
    // endregion

    private fun initListeners(teamList: List<Pair<String, Int>>) {
        onBtnDelete1Clicked(teamList)
        onBtnDelete2Clicked(teamList)
        onBtnDelete3Clicked(teamList)
        onBtnDelete4Clicked(teamList)
        onBtnDelete5Clicked(teamList)
        onBtnDelete6Clicked(teamList)
    }

    // region initListeners
    private fun onBtnDelete1Clicked(teamList: List<Pair<String, Int>>) {
        binding.btnDelete1.setOnClickListener {
            showSimpleAlertDialog(
                requireContext(),
                R.string.remove_from_my_team_title,
                R.string.remove_from_my_team_message,
                R.string.yes,
                R.string.no,
                { dialog, _ ->
                    viewModel.removeFromMyTeam(teamList[0].first)
                    dialog.dismiss()
                },
                { dialog, _ ->
                    dialog.cancel()
                }
            )
        }
    }

    private fun onBtnDelete2Clicked(teamList: List<Pair<String, Int>>) {
        binding.btnDelete2.setOnClickListener {
            showSimpleAlertDialog(
                requireContext(),
                R.string.remove_from_my_team_title,
                R.string.remove_from_my_team_message,
                R.string.yes,
                R.string.no,
                { dialog, _ ->
                    viewModel.removeFromMyTeam(teamList[1].first)
                    dialog.dismiss()
                },
                { dialog, _ ->
                    dialog.cancel()
                }
            )
        }
    }

    private fun onBtnDelete3Clicked(teamList: List<Pair<String, Int>>) {
        binding.btnDelete3.setOnClickListener {
            showSimpleAlertDialog(
                requireContext(),
                R.string.remove_from_my_team_title,
                R.string.remove_from_my_team_message,
                R.string.yes,
                R.string.no,
                { dialog, _ ->
                    viewModel.removeFromMyTeam(teamList[2].first)
                    dialog.dismiss()
                },
                { dialog, _ ->
                    dialog.cancel()
                }
            )
        }
    }

    private fun onBtnDelete4Clicked(teamList: List<Pair<String, Int>>) {
        binding.btnDelete4.setOnClickListener {
            showSimpleAlertDialog(
                requireContext(),
                R.string.remove_from_my_team_title,
                R.string.remove_from_my_team_message,
                R.string.yes,
                R.string.no,
                { dialog, _ ->
                    viewModel.removeFromMyTeam(teamList[3].first)
                    dialog.dismiss()
                },
                { dialog, _ ->
                    dialog.cancel()
                }
            )
        }
    }

    private fun onBtnDelete5Clicked(teamList: List<Pair<String, Int>>) {
        binding.btnDelete5.setOnClickListener {
            showSimpleAlertDialog(
                requireContext(),
                R.string.remove_from_my_team_title,
                R.string.remove_from_my_team_message,
                R.string.yes,
                R.string.no,
                { dialog, _ ->
                    viewModel.removeFromMyTeam(teamList[4].first)
                    dialog.dismiss()
                },
                { dialog, _ ->
                    dialog.cancel()
                }
            )
        }
    }

    private fun onBtnDelete6Clicked(teamList: List<Pair<String, Int>>) {
        binding.btnDelete6.setOnClickListener {
            showSimpleAlertDialog(
                requireContext(),
                R.string.remove_from_my_team_title,
                R.string.remove_from_my_team_message,
                R.string.yes,
                R.string.no,
                { dialog, _ ->
                    viewModel.removeFromMyTeam(teamList[5].first)
                    dialog.dismiss()
                },
                { dialog, _ ->
                    dialog.cancel()
                }
            )
        }
    }
    // endregion
}