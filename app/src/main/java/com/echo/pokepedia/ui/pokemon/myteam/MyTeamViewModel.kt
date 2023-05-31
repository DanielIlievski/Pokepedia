package com.echo.pokepedia.ui.pokemon.myteam

import androidx.lifecycle.viewModelScope
import com.echo.pokepedia.domain.pokemon.interactors.AddPokemonToMyTeamUseCase
import com.echo.pokepedia.domain.pokemon.interactors.GetMyTeamListUseCase
import com.echo.pokepedia.domain.pokemon.interactors.RemovePokemonFromMyTeamUseCase
import com.echo.pokepedia.domain.pokemon.model.PokemonDTO
import com.echo.pokepedia.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyTeamViewModel @Inject constructor(
    private val getMyTeamListUseCase: GetMyTeamListUseCase,
    private val removePokemonFromMyTeamUseCase: RemovePokemonFromMyTeamUseCase,
    private val addPokemonToMyTeamUseCase: AddPokemonToMyTeamUseCase
) : BaseViewModel() {

    private val _myTeamList: MutableStateFlow<List<PokemonDTO>> =
        MutableStateFlow(emptyList())
    val myTeamList: Flow<List<PokemonDTO>> get() = _myTeamList

    fun initTeam() {
        viewModelScope.launch {
            getMyTeamListUseCase.invoke().collect {
                _myTeamList.value = it
            }
        }
    }

    fun removeFromMyTeam(pokemonId: Int?) = viewModelScope.launch {
        pokemonId?.let { removePokemonFromMyTeamUseCase.invoke(it) }
    }

    fun addPokemonToMyTeam(pokemonId: Int?) = viewModelScope.launch {
        if (pokemonId != null) {
            addPokemonToMyTeamUseCase.invoke(pokemonId)
        }
    }
}