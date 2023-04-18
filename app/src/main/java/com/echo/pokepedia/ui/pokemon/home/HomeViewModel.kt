package com.echo.pokepedia.ui.pokemon.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echo.pokepedia.domain.pokemon.Pokemon
import com.echo.pokepedia.domain.pokemon.PokemonList
import com.echo.pokepedia.domain.pokemon.interactors.GetPokemonInfoFromApiUserCase
import com.echo.pokepedia.domain.pokemon.interactors.GetPokemonListFromApiUserCase
import com.echo.pokepedia.util.NetworkResult
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
    private var _pokemonList = MutableStateFlow<NetworkResult<PokemonList>>(
        NetworkResult.Success(PokemonList())
    )
    val pokemonList: StateFlow<NetworkResult<PokemonList>> get() = _pokemonList

    private var _pokemonInfo = MutableStateFlow<NetworkResult<Pokemon>>(
        NetworkResult.Success(Pokemon())
    )
    val pokemonInfo: StateFlow<NetworkResult<Pokemon>> get() = _pokemonInfo

    fun getPokemonList(limit: Int, offset: Int) = viewModelScope.launch {
        val response = getPokemonListFromApiUserCase.invoke(limit, offset)
        _pokemonList.value = response
    }

    fun getPokemonInfo(name: String) = viewModelScope.launch {
        val response = getPokemonInfoFromApiUserCase.invoke(name)
        _pokemonInfo.value = response
    }

}
