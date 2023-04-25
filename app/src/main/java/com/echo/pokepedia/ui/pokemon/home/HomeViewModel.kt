package com.echo.pokepedia.ui.pokemon.home

//import android.support.v7.graphics.Palette
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.echo.pokepedia.domain.pokemon.interactors.GetPokemonInfoFromApiUserCase
import com.echo.pokepedia.domain.pokemon.interactors.GetPokemonListFromApiUserCase
import com.echo.pokepedia.domain.pokemon.model.Pokemon
import com.echo.pokepedia.domain.pokemon.model.PokemonDetails
import com.echo.pokepedia.ui.BaseViewModel
import com.echo.pokepedia.util.NetworkResult
import com.echo.pokepedia.util.PAGE_SIZE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPokemonListFromApiUserCase: GetPokemonListFromApiUserCase,
    private val getPokemonInfoFromApiUserCase: GetPokemonInfoFromApiUserCase
) : BaseViewModel() {

    // region viewModel variables
    private var _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemonList: StateFlow<List<Pokemon>> get() = _pokemonList

    private var _pokemonDetailsInfo = MutableStateFlow<PokemonDetails>(PokemonDetails())
    val pokemonDetailsInfo: StateFlow<PokemonDetails> get() = _pokemonDetailsInfo

    private var currPage = 0

    private var _endReached = MutableStateFlow<Boolean>(false)
    val endReached: StateFlow<Boolean> get() = _endReached
    // endregion

    init {
        getPokemonListPaginated()
    }

    fun getPokemonListPaginated() = viewModelScope.launch {
        val response = getPokemonListFromApiUserCase.invoke(PAGE_SIZE, currPage * PAGE_SIZE)
        when (response) {
            is NetworkResult.Success -> {
                _endReached.value = currPage * PAGE_SIZE >= response.result.count!!
                currPage++
                _pokemonList.value += response.result.pokemonList!!
                Log.d("HelloWorld", "getPokemonListPaginated: ${_pokemonList.value.size}")
            }
            is NetworkResult.Failure -> _errorObservable.value = response.exception!!
        }
    }

    fun getPokemonInfo(name: String) = viewModelScope.launch {
        val response = getPokemonInfoFromApiUserCase.invoke(name)
        when (response) {
            is NetworkResult.Success -> _pokemonDetailsInfo.value = response.result
            is NetworkResult.Failure -> _errorObservable.value = response.exception!!
        }
    }

}
