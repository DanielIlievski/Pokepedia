package com.echo.pokepedia.ui.pokemon.myteam

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
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

    private val args by navArgs<MyTeamFragmentArgs>()

    private val myImageViewList = mutableListOf<ImageView>()

    private val myBtnDeleteList = mutableListOf<ImageButton>()

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

        initImageViewList()

        initBtnDeleteList()

        initObservers()

        initListeners()
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

    // region observeMyTeamList
    private fun observeMyTeamList() = lifecycleScope.launch {
        viewModel.myTeamList.collect { teamList ->
            myBtnDeleteList.forEachIndexed { i, _ ->
                if (i < teamList.size) {
                    myImageViewList[i].background = getGradientWhiteBottom(teamList[i].second)
                    loadImage(teamList[i].first, myImageViewList[i])
                    startShakeAnimation(myImageViewList[i])
                    myBtnDeleteList[i].visibility = View.VISIBLE
                } else {
                    myBtnDeleteList[i].visibility = View.GONE
                    myImageViewList[i].backgroundTintList =
                        ColorStateList.valueOf(requireContext().getColorRes(R.color.grey_light))
                    myImageViewList[i].setImageResource(R.drawable.ic_add_pokemon)
                    myImageViewList[i].animation = null
                }
            }
        }
    }

    private fun startShakeAnimation(view: View) {
        view.animation =
            if (args.isTeamFull)
                AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
            else null
    }

    private fun stopAllShakeAnimation() {
        myImageViewList.forEach { it.animation = null }
    }
    // endregion
    // endregion

    private fun initListeners() {
        onBtnDelete1Clicked()
        onBtnDelete2Clicked()
        onBtnDelete3Clicked()
        onBtnDelete4Clicked()
        onBtnDelete5Clicked()
        onBtnDelete6Clicked()
    }

    // region initListeners
    private fun onBtnDelete1Clicked() {
        binding.btnDelete1.setOnClickListener {
            alertDialogDeleteOnPosition(0)
        }
    }

    private fun onBtnDelete2Clicked() {
        binding.btnDelete2.setOnClickListener {
            alertDialogDeleteOnPosition(1)
        }
    }

    private fun onBtnDelete3Clicked() {
        binding.btnDelete3.setOnClickListener {
            alertDialogDeleteOnPosition(2)
        }
    }

    private fun onBtnDelete4Clicked() {
        binding.btnDelete4.setOnClickListener {
            alertDialogDeleteOnPosition(3)
        }
    }

    private fun onBtnDelete5Clicked() {
        binding.btnDelete5.setOnClickListener {
            alertDialogDeleteOnPosition(4)
        }
    }

    private fun onBtnDelete6Clicked() {
        binding.btnDelete6.setOnClickListener {
            alertDialogDeleteOnPosition(5)
        }
    }
    // endregion

    private fun alertDialogDeleteOnPosition(position: Int) {
        showSimpleAlertDialog(
            context = requireContext(),
            title = R.string.remove_from_my_team_title,
            message = R.string.remove_from_my_team_message,
            positiveBtnText = R.string.yes,
            negativeBtnText = R.string.no,
            onPositiveBtnClick = { dialog, _ ->
                deleteOnPosition(position)
                if (isTeamFull) {
                    isTeamFull = false
                    viewModel.addPokemonToMyTeam(args.imgUrl, args.dominantColor)
                }
                stopAllShakeAnimation()
                dialog.dismiss()
            },
            onNegativeBtnClick = { dialog, _ ->
                dialog.cancel()
            }
        )
    }

    private fun deleteOnPosition(position: Int) {
        if (position in 0 until viewModel.getMyTeamList().size) {
            viewModel.removeFromMyTeam(viewModel.getMyTeamList()[position].first)
        }
    }
}