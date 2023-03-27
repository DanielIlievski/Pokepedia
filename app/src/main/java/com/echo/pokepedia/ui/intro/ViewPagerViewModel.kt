package com.echo.pokepedia.ui.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewPagerViewModel @Inject constructor() : ViewModel() {

    private val _isLastScreen = MutableLiveData(false)
    val isLastScreen: LiveData<Boolean> get() = _isLastScreen

    fun setIsLastScreen(isLastScreen: Boolean) {
        _isLastScreen.value = isLastScreen
    }
}