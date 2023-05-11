package com.echo.pokepedia.ui.pokemon.myteam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echo.pokepedia.domain.pokemon.interactors.GetMyTeamListUseCase
import com.echo.pokepedia.domain.pokemon.interactors.RemovePokemonFromMyTeamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyTeamViewModel @Inject constructor(
    private val getMyTeamListUseCase: GetMyTeamListUseCase,
    private val removePokemonFromMyTeamUseCase: RemovePokemonFromMyTeamUseCase
) : ViewModel() {

    private var _myTeamList: MutableStateFlow<List<Pair<String, Int>>> =
        MutableStateFlow(emptyList())
    val myTeamList: Flow<List<Pair<String, Int>>> get() = _myTeamList

    fun initTeam() {
        viewModelScope.launch {
            getMyTeamListUseCase.invoke().collect {
                _myTeamList.value = it
            }
        }
    }

    fun removeFromMyTeam(imgUrl: String) {
        removePokemonFromMyTeamUseCase.invoke(imgUrl)
        initTeam()
    }
}