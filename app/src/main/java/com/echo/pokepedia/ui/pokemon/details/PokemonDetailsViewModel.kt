package com.echo.pokepedia.ui.pokemon.details

import androidx.lifecycle.viewModelScope
import com.echo.pokepedia.domain.pokemon.interactors.GetPokemonInfoFromApiUserCase
import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO
import com.echo.pokepedia.ui.BaseViewModel
import com.echo.pokepedia.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailsViewModel @Inject constructor(
    private val getPokemonInfoFromApiUserCase: GetPokemonInfoFromApiUserCase
) : BaseViewModel() {

    // region viewModel variables
    private var _pokemonDetailsInfo = MutableStateFlow<PokemonDetailsDTO>(PokemonDetailsDTO())
    val pokemonDetailsInfo: StateFlow<PokemonDetailsDTO> get() = _pokemonDetailsInfo

    private var _pokemonStats = MutableStateFlow<List<Triple<String, Int, Int>>?>(emptyList())
    val pokemonStats: StateFlow<List<Triple<String, Int, Int>>?> get() = _pokemonStats
    // endregion

    fun getPokemonDetails(name: String) = viewModelScope.launch {
        val response = getPokemonInfoFromApiUserCase.invoke(name)
        when (response) {
            is NetworkResult.Success -> {
                _pokemonDetailsInfo.value = response.result
                _pokemonStats.value = response.result.stats
            }
            is NetworkResult.Failure -> _errorObservable.value = response.exception!!
        }
    }

    fun refreshStats() {
        _pokemonStats.value = emptyList()
        _pokemonStats.value = _pokemonDetailsInfo.value.stats
    }
}