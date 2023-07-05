package com.echo.pokepedia.ui.splash

import com.echo.pokepedia.data.preferences.SettingsDataStore
import com.echo.pokepedia.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    settingsDataStore: SettingsDataStore
) : BaseViewModel() {

    // region viewModel variables
    private val _isOnBoardingAvailable = settingsDataStore.onBoardingPreferencesFlow
    val isOnBoardingAvailable: Flow<Boolean> get() = _isOnBoardingAvailable
    // endregion
}