package com.echo.pokepedia.ui.pokemon.home

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.echo.pokepedia.data.preferences.SettingsDataStore
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPokemonListFromApiUserCase: GetPokemonListFromApiUserCase,
    private val getPokemonInfoFromApiUserCase: GetPokemonInfoFromApiUserCase,
    settingsDataStore: SettingsDataStore
) : BaseViewModel() {

    // region viewModel variables
    private var _pokemonList = MutableStateFlow<PagingData<PokemonDTO>>(PagingData.empty())
    val pokemonList: StateFlow<PagingData<PokemonDTO>> get() = _pokemonList

    private var _buddyPokemonDetails = MutableStateFlow<PokemonDetailsDTO?>(null)
    val buddyPokemonDetails: StateFlow<PokemonDetailsDTO?> get() = _buddyPokemonDetails

    private var _buddyPokemonName = settingsDataStore.buddyPokemonNameFlow

    private var _buddyPokemonNickname = settingsDataStore.buddyPokemonNicknameFlow
    val buddyPokemonNickname get() = _buddyPokemonNickname.asLiveData()

    private var _buddyPokemonDominantColor = settingsDataStore.buddyPokemonDominantColorFlow
    val buddyPokemonDominantColor get() = _buddyPokemonDominantColor.asLiveData()
    // endregion

    init {
        getPokemonListPaginated()
    }

    private fun getPokemonListPaginated() {
        viewModelScope.launch(Dispatchers.IO) {
            getPokemonListFromApiUserCase.invoke()
                .cachedIn(viewModelScope)
                .collect {
                    _pokemonList.emit(it)
                }
        }
    }

    fun getPokemonInfo() = viewModelScope.launch {
        val response = getPokemonInfoFromApiUserCase.invoke(_buddyPokemonName.first())
        when (response) {
            is NetworkResult.Success -> _buddyPokemonDetails.value = response.result
            is NetworkResult.Failure -> _errorObservable.value = response.exception!!
        }
    }

    fun clearBuddyPokemonDetails() {
        _buddyPokemonDetails.value = null
    }

}
