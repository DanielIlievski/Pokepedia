package com.echo.pokepedia.ui.pokemon.myteam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.FragmentMyTeamBinding
import com.echo.pokepedia.ui.BaseFragment

class MyTeamFragment : BaseFragment() {

    // region fragment variables
    private var _binding: FragmentMyTeamBinding? = null
    private val binding get() = _binding!!
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

        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
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
            binding.imgPokemon1.setImageResource(R.drawable.ic_add_pokemon)
        }
    }

    private fun onBtnDelete2Clicked() {
        binding.btnDelete2.setOnClickListener {
            binding.imgPokemon2.setImageResource(R.drawable.ic_add_pokemon)
        }
    }

    private fun onBtnDelete3Clicked() {
        binding.btnDelete3.setOnClickListener {
            binding.imgPokemon3.setImageResource(R.drawable.ic_add_pokemon)
        }
    }

    private fun onBtnDelete4Clicked() {
        binding.btnDelete4.setOnClickListener {
            binding.imgPokemon4.setImageResource(R.drawable.ic_add_pokemon)
        }
    }

    private fun onBtnDelete5Clicked() {
        binding.btnDelete5.setOnClickListener {
            binding.imgPokemon5.setImageResource(R.drawable.ic_add_pokemon)
        }
    }

    private fun onBtnDelete6Clicked() {
        binding.btnDelete6.setOnClickListener {
            binding.imgPokemon6.setImageResource(R.drawable.ic_add_pokemon)
        }
    }
    // endregion
}