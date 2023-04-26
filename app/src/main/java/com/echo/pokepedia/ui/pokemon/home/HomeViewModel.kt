package com.echo.pokepedia.ui.pokemon.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echo.pokepedia.domain.pokemon.interactors.GetPokemonInfoFromApiUserCase
import com.echo.pokepedia.domain.pokemon.interactors.GetPokemonListFromApiUserCase
import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO
import com.echo.pokepedia.domain.pokemon.model.PokemonListDTO
import com.echo.pokepedia.util.NetworkResult
import com.echo.pokepedia.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPokemonListFromApiUserCase: GetPokemonListFromApiUserCase,
    private val getPokemonInfoFromApiUserCase: GetPokemonInfoFromApiUserCase
) : ViewModel() {

    // region viewModel variables
    private var _pokemonList = MutableStateFlow<PokemonListDTO>(PokemonListDTO())
    val pokemonList: StateFlow<PokemonListDTO> get() = _pokemonList

    private var _pokemonDetailsInfo = MutableStateFlow<PokemonDetailsDTO>(PokemonDetailsDTO())
    val pokemonDetailsInfo: StateFlow<PokemonDetailsDTO> get() = _pokemonDetailsInfo

    private var _errorObservable  = MutableStateFlow<UiText>(UiText.DynamicString())
    val errorObservable : StateFlow<UiText> get() = _errorObservable
    // endregion

    fun getPokemonList(limit: Int, offset: Int) = viewModelScope.launch {
        val response = getPokemonListFromApiUserCase.invoke(limit, offset)
        when (response) {
            is NetworkResult.Success -> _pokemonList.value = response.result
            is NetworkResult.Failure -> _errorObservable.value = response.exception!!
        }
    }

    fun getPokemonInfo(name: String) = viewModelScope.launch {
        val response = getPokemonInfoFromApiUserCase.invoke(name)
        when (response) {
            is NetworkResult.Success -> _pokemonDetailsInfo.value = response.result
            is NetworkResult.Failure -> response.exception?.let { _errorObservable.value }
        }
    }

}
