package com.echo.pokepedia.ui.pokemon.details

import androidx.lifecycle.viewModelScope
import com.echo.pokepedia.data.preferences.SettingsDataStore
import com.echo.pokepedia.domain.pokemon.interactors.AddPokemonToMyTeamUseCase
import com.echo.pokepedia.domain.pokemon.interactors.GetMyTeamListUseCase
import com.echo.pokepedia.domain.pokemon.interactors.GetPokemonInfoFromApiUseCase
import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO
import com.echo.pokepedia.ui.BaseViewModel
import com.echo.pokepedia.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailsViewModel @Inject constructor(
    private val getPokemonInfoFromApiUseCase: GetPokemonInfoFromApiUseCase,
    private val addPokemonToMyTeamUseCase: AddPokemonToMyTeamUseCase,
    private val getMyTeamListUseCase: GetMyTeamListUseCase,
    private val settingsDataStore: SettingsDataStore
) : BaseViewModel() {

    // region viewModel variables
    private var _pokemonDetailsInfo = MutableStateFlow<PokemonDetailsDTO>(PokemonDetailsDTO())
    val pokemonDetailsInfo: StateFlow<PokemonDetailsDTO> get() = _pokemonDetailsInfo

    private var _pokemonStats = MutableStateFlow<List<Triple<String, Int, Int>>?>(emptyList())
    val pokemonStats: StateFlow<List<Triple<String, Int, Int>>?> get() = _pokemonStats

    private var _buddyPokemonName = settingsDataStore.buddyPokemonNameFlow
    // endregion

    fun getPokemonDetails(name: String) = viewModelScope.launch {
        val response = getPokemonInfoFromApiUseCase.invoke(name)
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

    fun setBuddyPokemonName(pokemonName: String) = viewModelScope.launch {
        settingsDataStore.saveBuddyPokemonIdToPreferencesStore(pokemonName)
    }

    fun setBuddyPokemonNickname(pokemonNickname: String) = viewModelScope.launch {
        settingsDataStore.saveBuddyPokemonNicknameToPreferencesStore(pokemonNickname)
    }

    fun setBuddyPokemonDominantColor(pokemonDominantColor: Int) = viewModelScope.launch {
        settingsDataStore.saveBuddyPokemonDominantColorToPreferencesStore(pokemonDominantColor)
    }

    suspend fun isPokemonFavorite(): Boolean {
        return _buddyPokemonName.first() == _pokemonDetailsInfo.value.name
    }

    suspend fun checkAddConditions(imgUrl: String?, dominantColor: Int): AddPokemonState {
        val myTeamList = getMyTeamListUseCase.invoke().firstOrNull() ?: emptyList()
        return when {
            myTeamList.contains(imgUrl to dominantColor) -> AddPokemonState.AlreadyExists
            myTeamList.size < 6 -> AddPokemonState.AddPokemon
            else -> AddPokemonState.TeamFull
        }
    }

    fun addPokemonToMyTeam(imgUrl: String?, dominantColor: Int) {
        imgUrl?.let { addPokemonToMyTeamUseCase.invoke(it, dominantColor) }
    }
}

sealed class AddPokemonState {
    object AddPokemon : AddPokemonState()
    object AlreadyExists : AddPokemonState()
    object TeamFull : AddPokemonState()
}
