package com.echo.pokepedia.ui

import androidx.lifecycle.ViewModel
import com.echo.pokepedia.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {

    protected val _errorObservable = MutableSharedFlow<UiText>()
    val errorObservable: SharedFlow<UiText> get() = _errorObservable
}