package com.echo.pokepedia.ui

import androidx.lifecycle.ViewModel
import com.echo.pokepedia.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {

    protected var _errorObservable = MutableStateFlow<UiText>(UiText.DynamicString())
    val errorObservable: StateFlow<UiText> get() = _errorObservable
}