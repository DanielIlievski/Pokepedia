package com.echo.pokepedia.ui.pokemon.home

//import android.support.v7.graphics.Palette
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echo.pokepedia.domain.pokemon.interactors.GetPokemonInfoFromApiUserCase
import com.echo.pokepedia.domain.pokemon.interactors.GetPokemonListFromApiUserCase
import com.echo.pokepedia.domain.pokemon.model.PokemonDetails
import com.echo.pokepedia.domain.pokemon.model.PokemonList
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

    private var _pokemonDetailsInfo = MutableStateFlow<NetworkResult<PokemonDetails>>(
        NetworkResult.Success(PokemonDetails())
    )
    val pokemonDetailsInfo: StateFlow<NetworkResult<PokemonDetails>> get() = _pokemonDetailsInfo

    fun getPokemonList(limit: Int, offset: Int) = viewModelScope.launch {
        val response = getPokemonListFromApiUserCase.invoke(limit, offset)
        _pokemonList.value = response
    }

    fun getPokemonInfo(name: String) = viewModelScope.launch {
        val response = getPokemonInfoFromApiUserCase.invoke(name)
        _pokemonDetailsInfo.value = response
    }

}
