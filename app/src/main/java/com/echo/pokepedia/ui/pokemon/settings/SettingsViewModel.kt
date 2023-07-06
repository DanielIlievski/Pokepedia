package com.echo.pokepedia.ui.pokemon.settings

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.echo.pokepedia.domain.authentication.interactors.GetCurrentUserUseCase
import com.echo.pokepedia.domain.authentication.interactors.LogoutUserUseCase
import com.echo.pokepedia.domain.authentication.interactors.UpdateUserProfilePhotoUseCase
import com.echo.pokepedia.domain.authentication.model.User
import com.echo.pokepedia.ui.BaseViewModel
import com.echo.pokepedia.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val updateUserProfilePhotoUseCase: UpdateUserProfilePhotoUseCase
) : BaseViewModel() {

    // region viewModel variables
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> get() = _currentUser

    private val _settingsViewState =
        MutableStateFlow<SettingsViewState>(SettingsViewState.EmptyViewState)
    val settingsViewState: StateFlow<SettingsViewState> get() = _settingsViewState
    // endregion

    init {
        getUser()
    }

    private fun getUser() = viewModelScope.launch(Dispatchers.IO) {
        val result = getCurrentUserUseCase.invoke()
        when (result) {
            is NetworkResult.Success -> result.result.collectLatest { _currentUser.value = it }
            is NetworkResult.Failure -> _errorObservable.emit(result.exception)
        }

    }

    fun logout() = viewModelScope.launch(Dispatchers.IO) {
        val logoutResponse = logoutUserUseCase.invoke()
        when (logoutResponse) {
            is NetworkResult.Success -> {
                _settingsViewState.value = SettingsViewState.LogoutSuccessful
            }
            is NetworkResult.Failure -> {
                _errorObservable.emit(logoutResponse.exception)
            }
        }
    }

    fun updateProfilePhoto(imgUri: Uri?) = viewModelScope.launch(Dispatchers.IO) {
        _settingsViewState.value = SettingsViewState.LoadingState
        val updatePhotoResponse = updateUserProfilePhotoUseCase.invoke(imgUri)
        when (updatePhotoResponse) {
            is NetworkResult.Success -> {
                _settingsViewState.value = SettingsViewState.UpdatePhotoSuccessful
                _settingsViewState.value = SettingsViewState.EmptyViewState
            }
            is NetworkResult.Failure -> {
                _errorObservable.emit(updatePhotoResponse.exception)
                _settingsViewState.value = SettingsViewState.EmptyViewState
            }
        }
    }
}

sealed class SettingsViewState {
    object LogoutSuccessful : SettingsViewState()
    object EmptyViewState : SettingsViewState()
    object UpdatePhotoSuccessful : SettingsViewState()
    object LoadingState : SettingsViewState()
}