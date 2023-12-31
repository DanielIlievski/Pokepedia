package com.echo.pokepedia.ui.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.echo.pokepedia.data.preferences.SettingsDataStore
import com.echo.pokepedia.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroScreensViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : BaseViewModel() {

    private val _isOnBoardingAvailable = settingsDataStore.onBoardingPreferencesFlow
    val isOnBoardingAvailable get() = _isOnBoardingAvailable.asLiveData()

    private val _isLastScreen = MutableLiveData(false)
    val isLastScreen: LiveData<Boolean> get() = _isLastScreen

    fun setIsLastScreen(isLastScreen: Boolean) {
        _isLastScreen.value = isLastScreen
    }

    fun setIsOnBoardingAvailable(isAvailable: Boolean) {
        viewModelScope.launch {
            settingsDataStore.saveOnBoardingToPreferencesStore(isAvailable)
        }
    }
}