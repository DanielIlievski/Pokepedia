package com.echo.pokepedia.ui.authentication.login

import androidx.lifecycle.viewModelScope
import com.echo.pokepedia.domain.authentication.interactors.RegisterUserUseCase
import com.echo.pokepedia.ui.BaseViewModel
import com.echo.pokepedia.util.*
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
) : BaseViewModel() {

    private val _viewState = MutableStateFlow<RegisterViewState>(RegisterViewState.EmptyViewState)
    val viewState: StateFlow<RegisterViewState> get() = _viewState

    private val _registerUser = MutableSharedFlow<NetworkResult<FirebaseUser?>>()
    val registerUser: SharedFlow<NetworkResult<FirebaseUser?>> get() = _registerUser

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        repeatPassword: String
    ) = viewModelScope.launch {
        if (isFirstNameFieldEmpty(firstName)) {
            _viewState.value = RegisterViewState.EmptyFirstNameField
        }
        if (isLastNameFieldEmpty(lastName)) {
            _viewState.value = RegisterViewState.EmptyLastNameField
        }
        if (isEmailFieldEmpty(email)) {
            _viewState.value = RegisterViewState.EmptyEmailField
        } else {
            if (!isValidEmail(email)) {
                _viewState.value = RegisterViewState.InvalidEmail
            }
        }
        if (isPasswordFieldEmpty(password)) {
            _viewState.value = RegisterViewState.EmptyPasswordField
        } else {
            if (!isPasswordStrong(password)) {
                _viewState.value = RegisterViewState.WeakPassword
            }
            if (!doPasswordsMatch(password, repeatPassword)) {
                _viewState.value = RegisterViewState.PasswordsDoNotMatch
            }
        }
        if (isValidEmail(email) &&
            isPasswordStrong(password) &&
            doPasswordsMatch(password, repeatPassword)
        ) {
            _registerUser.emit(registerUserUseCase.invoke(firstName, lastName, email, password))
        }
    }

}

sealed class RegisterViewState {
    object EmptyViewState : RegisterViewState()
    object PasswordsDoNotMatch : RegisterViewState()
    object InvalidEmail : RegisterViewState()
    object WeakPassword : RegisterViewState()
    object EmptyEmailField : RegisterViewState()
    object EmptyPasswordField : RegisterViewState()
    object EmptyFirstNameField : RegisterViewState()
    object EmptyLastNameField : RegisterViewState()
}