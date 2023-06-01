package com.echo.pokepedia.ui.pokemon.settings

import androidx.lifecycle.viewModelScope
import com.echo.pokepedia.domain.authentication.interactors.GetCurrentUserUseCase
import com.echo.pokepedia.domain.authentication.interactors.LogoutUserUseCase
import com.echo.pokepedia.domain.authentication.model.User
import com.echo.pokepedia.ui.BaseViewModel
import com.echo.pokepedia.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUserUseCase: LogoutUserUseCase
) : BaseViewModel() {

    // region viewModel variables
    private val _currentUser = MutableStateFlow<User>(User())
    val currentUser: StateFlow<User> get() = _currentUser

    private val _settingsViewState =
        MutableStateFlow<SettingsViewState>(SettingsViewState.EmptyViewState)
    val settingsViewState: StateFlow<SettingsViewState> get() = _settingsViewState
    // endregion

    init {
        getUser()
    }

    private fun getUser() = viewModelScope.launch {
        val userResult = getCurrentUserUseCase.invoke()
        when (userResult) {
            is NetworkResult.Success -> _currentUser.value = userResult.result
            is NetworkResult.Failure -> _errorObservable.value = userResult.exception
        }
    }

    fun logout() = viewModelScope.launch {
        val logoutResult = logoutUserUseCase.invoke()
        when (logoutResult) {
            is NetworkResult.Success -> {
                _settingsViewState.value = SettingsViewState.LogoutSuccessful
            }
            is NetworkResult.Failure -> {
                _errorObservable.value = logoutResult.exception
            }
        }
    }
}

sealed class SettingsViewState {
    object LogoutSuccessful : SettingsViewState()
    object EmptyViewState : SettingsViewState()
}