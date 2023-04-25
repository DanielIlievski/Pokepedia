package com.echo.pokepedia.ui.pokemon.home

//import android.support.v7.graphics.Palette
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.echo.pokepedia.domain.pokemon.interactors.GetPokemonInfoFromApiUserCase
import com.echo.pokepedia.domain.pokemon.interactors.GetPokemonListFromApiUserCase
import com.echo.pokepedia.domain.pokemon.model.Pokemon
import com.echo.pokepedia.domain.pokemon.model.PokemonDetails
import com.echo.pokepedia.ui.BaseViewModel
import com.echo.pokepedia.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPokemonListFromApiUserCase: GetPokemonListFromApiUserCase,
    private val getPokemonInfoFromApiUserCase: GetPokemonInfoFromApiUserCase
) : BaseViewModel() {

    // region viewModel variables
    private var _pokemonList = MutableSharedFlow<PagingData<Pokemon>>(replay = 0)
    val pokemonList: SharedFlow<PagingData<Pokemon>> get() = _pokemonList


    private var _pokemonDetailsInfo = MutableStateFlow<PokemonDetails>(PokemonDetails())
    val pokemonDetailsInfo: StateFlow<PokemonDetails> get() = _pokemonDetailsInfo

    private var currPage = 0

    private var _endReached = MutableStateFlow<Boolean>(false)
    val endReached: StateFlow<Boolean> get() = _endReached
    // endregion

    init {
        getPokemonListPaginated()
    }

//    fun getPokemonListPaginated() = viewModelScope.launch {
//        val response = getPokemonListFromApiUserCase.invoke(PAGE_SIZE, currPage * PAGE_SIZE)
//        when (response) {
//            is NetworkResult.Success -> {
//                _endReached.value = currPage * PAGE_SIZE >= response.result.count!!
//                currPage++
//                _pokemonList.value += response.result.pokemonList!!
//                Log.d("HelloWorld", "getPokemonListPaginated: ${_pokemonList.value.size}")
//            }
//            is NetworkResult.Failure -> _errorObservable.value = response.exception!!
//        }
//    }

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
