package com.echo.pokepedia.ui.pokemon.home

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.echo.pokepedia.data.preferences.SettingsDataStore
import com.echo.pokepedia.domain.pokemon.interactors.GetPokemonInfoFromApiUseCase
import com.echo.pokepedia.domain.pokemon.interactors.GetPokemonListFromApiUseCase
import com.echo.pokepedia.domain.pokemon.interactors.SearchPokemonsByNameOrIdUseCase
import com.echo.pokepedia.domain.pokemon.model.PokemonDTO
import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO
import com.echo.pokepedia.ui.BaseViewModel
import com.echo.pokepedia.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPokemonListFromApiUseCase: GetPokemonListFromApiUseCase,
    private val getPokemonInfoFromApiUseCase: GetPokemonInfoFromApiUseCase,
    private val searchPokemonsByNameOrIdUseCase: SearchPokemonsByNameOrIdUseCase,
    settingsDataStore: SettingsDataStore
) : BaseViewModel() {

    // region viewModel variables
    private val _pokemonList = MutableSharedFlow<PagingData<PokemonDTO>>()
    val pokemonList: SharedFlow<PagingData<PokemonDTO>> get() = _pokemonList

    private val _buddyPokemonDetails = MutableStateFlow<PokemonDetailsDTO?>(null)
    val buddyPokemonDetails: StateFlow<PokemonDetailsDTO?> get() = _buddyPokemonDetails

    private val _buddyPokemonName = settingsDataStore.buddyPokemonNameFlow

    private val _buddyPokemonNickname = settingsDataStore.buddyPokemonNicknameFlow
    val buddyPokemonNickname get() = _buddyPokemonNickname.asLiveData()

    private val _buddyPokemonDominantColor = settingsDataStore.buddyPokemonDominantColorFlow
    val buddyPokemonDominantColor get() = _buddyPokemonDominantColor.asLiveData()

    private val _queriedPokemonList = MutableSharedFlow<List<PokemonDTO>>()
    val queriedPokemonList: SharedFlow<List<PokemonDTO>> get() = _queriedPokemonList

    private val _homeViewState = MutableStateFlow<HomeViewState?>(null)
    val homeViewState: StateFlow<HomeViewState?> get() = _homeViewState
    // endregion

    fun getPokemonListPaginated() = viewModelScope.launch(Dispatchers.IO) {
        _homeViewState.emit(HomeViewState.ShowPokemonListPaginated)
        getPokemonListFromApiUseCase.invoke()
            .cachedIn(viewModelScope)
            .collect {
                _pokemonList.emit(it)
            }
    }

    private fun getQueriedPokemonList(query: String) = viewModelScope.launch(Dispatchers.IO) {
        _homeViewState.emit(HomeViewState.ShowQueriedPokemonList)
        searchPokemonsByNameOrIdUseCase.invoke(query).collect {
            _queriedPokemonList.emit(it)
        }
    }

    fun getPokemonInfo() = viewModelScope.launch(Dispatchers.IO) {
        _buddyPokemonName.collect {
            if (it.isNotEmpty()) {
                val response = getPokemonInfoFromApiUseCase.invoke(_buddyPokemonName.first())
                when (response) {
                    is NetworkResult.Success -> _buddyPokemonDetails.value = response.result
                    is NetworkResult.Failure -> _errorObservable.value = response.exception
                }
            }
        }
    }

    fun clearBuddyPokemonDetails() {
        _buddyPokemonDetails.value = null
    }

    fun searchPokemonList(query: String) = viewModelScope.launch(Dispatchers.IO) {
        if (query.isEmpty()) {
            getPokemonListPaginated()
        } else {
            getQueriedPokemonList(query)
        }
    }
}

sealed class HomeViewState {
    object ShowPokemonListPaginated : HomeViewState()
    object ShowQueriedPokemonList : HomeViewState()
}