package com.echo.pokepedia.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echo.pokepedia.domain.usecases.LoginUserUseCase
import com.echo.pokepedia.util.Resource
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    private var _viewState = MutableStateFlow<LoginViewState>(LoginViewState.EmptyViewState)
    val viewState: StateFlow<LoginViewState> get() = _viewState

    private var _loginUser =
        MutableSharedFlow<Resource<FirebaseUser?>>()
    val loginUser: SharedFlow<Resource<FirebaseUser?>> = _loginUser

    fun login(email: String, password: String) {
        if (isEmailFieldEmpty(email)) {
            _viewState.value = LoginViewState.EmptyEmailField
        }
        if (isPasswordFieldEmpty(password)) {
            _viewState.value = LoginViewState.EmptyPasswordField
        }
        if (!isEmailFieldEmpty(email) && !isPasswordFieldEmpty(password)) {
            viewModelScope.launch {
                _loginUser.emit(loginUserUseCase.invoke(email, password))
            }
        }
    }

    private fun isEmailFieldEmpty(email: String): Boolean {
        return email.isBlank() || email.isEmpty()
    }

    private fun isPasswordFieldEmpty(password: String): Boolean {
        return password.isBlank() || password.isEmpty()
    }
}

sealed class LoginViewState {
    object EmptyViewState : LoginViewState()
    object EmptyPasswordField : LoginViewState()
    object EmptyEmailField : LoginViewState()
}