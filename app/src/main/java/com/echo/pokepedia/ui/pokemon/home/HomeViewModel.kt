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
    private var _pokemonList = MutableSharedFlow<PagingData<PokemonDTO>>()
    val pokemonList: SharedFlow<PagingData<PokemonDTO>> get() = _pokemonList

    private var _buddyPokemonDetails = MutableStateFlow<PokemonDetailsDTO?>(null)
    val buddyPokemonDetails: StateFlow<PokemonDetailsDTO?> get() = _buddyPokemonDetails

    private var _buddyPokemonName = settingsDataStore.buddyPokemonNameFlow

    private var _buddyPokemonNickname = settingsDataStore.buddyPokemonNicknameFlow
    val buddyPokemonNickname get() = _buddyPokemonNickname.asLiveData()

    private var _buddyPokemonDominantColor = settingsDataStore.buddyPokemonDominantColorFlow
    val buddyPokemonDominantColor get() = _buddyPokemonDominantColor.asLiveData()

    private var _queriedPokemonList = MutableSharedFlow<List<PokemonDTO>>()
    val queriedPokemonList: SharedFlow<List<PokemonDTO>> get() = _queriedPokemonList

    private var _homeViewState = MutableSharedFlow<HomeViewState>()
    val homeViewState: SharedFlow<HomeViewState> get() = _homeViewState

    private var _emptyViewState = MutableSharedFlow<EmptyViewState>()
    val emptyViewState: SharedFlow<EmptyViewState> get() = _emptyViewState
    // endregion

    fun getPokemonListPaginated() {
        viewModelScope.launch(Dispatchers.IO) {
            getPokemonListFromApiUseCase.invoke()
                .cachedIn(viewModelScope)
                .collect {
                    _pokemonList.emit(it)
                }
        }
    }

    private fun getQueriedPokemonList(query: String) {
        viewModelScope.launch {
            searchPokemonsByNameOrIdUseCase.invoke(query).collect {
                _queriedPokemonList.emit(it)
            }
        }
    }

    fun getPokemonInfo() = viewModelScope.launch {
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

    fun searchPokemonList(query: String) = viewModelScope.launch {
        if (query.isEmpty()) {
            _homeViewState.emit(HomeViewState.ShowPokemonListPaginated)
            getPokemonListPaginated()
        } else {
            _homeViewState.emit(HomeViewState.ShowQueriedPokemonList)
            getQueriedPokemonList(query)
        }
    }

    fun setEmptyViewState(viewState: EmptyViewState) {
        viewModelScope.launch {
            _emptyViewState.emit(viewState)
        }
    }
}

sealed class HomeViewState {
    object ShowPokemonListPaginated : HomeViewState()
    object ShowQueriedPokemonList : HomeViewState()
}

sealed class EmptyViewState {
    object PokemonListEmptyState : EmptyViewState()
    object QueriedListEmptyState : EmptyViewState()
    object HideEmptyState : EmptyViewState()
}
