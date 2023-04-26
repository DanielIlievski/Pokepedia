package com.echo.pokepedia.ui.pokemon.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.echo.pokepedia.domain.pokemon.interactors.GetPokemonInfoFromApiUserCase
import com.echo.pokepedia.domain.pokemon.interactors.GetPokemonListFromApiUserCase
import com.echo.pokepedia.domain.pokemon.model.PokemonDTO
import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO
import com.echo.pokepedia.ui.BaseViewModel
import com.echo.pokepedia.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private var _pokemonList = MutableStateFlow<PagingData<PokemonDTO>>(PagingData.empty())
    val pokemonList: StateFlow<PagingData<PokemonDTO>> get() = _pokemonList

    private var _pokemonDetailsInfo = MutableStateFlow<PokemonDetailsDTO>(PokemonDetailsDTO())
    val pokemonDetailsInfo: StateFlow<PokemonDetailsDTO> get() = _pokemonDetailsInfo
    // endregion

    init {
        getPokemonListPaginated()
    }

    fun getPokemonListPaginated() {
        viewModelScope.launch(Dispatchers.IO) {
            getPokemonListFromApiUserCase.invoke()
                .cachedIn(viewModelScope)
                .collect {
                    _pokemonList.emit(it)
                }
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
