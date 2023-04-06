package com.echo.pokepedia.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echo.pokepedia.domain.usecases.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Patterns.EMAIL_ADDRESS
import com.echo.pokepedia.util.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
) : ViewModel() {

    private var _viewState = MutableStateFlow<RegisterViewState>(RegisterViewState.EmptyViewState)
    val viewState: StateFlow<RegisterViewState> get() = _viewState

    private var _registerUser =
        MutableSharedFlow<Resource<FirebaseUser?>>()
    val registerUser: SharedFlow<Resource<FirebaseUser?>> get() = _registerUser

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

    private fun isValidEmail(email: String): Boolean {
        return EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun doPasswordsMatch(password: String, repeatPassword: String): Boolean {
        return password == repeatPassword
    }

    private fun isPasswordStrong(password: String): Boolean {
        val capitalRegex = Regex("[A-Z]")
        val numberRegex = Regex("\\d")
        val specialCharRegex = Regex("[^A-Za-z\\d]")

        return password.length >= 8 &&
                password.contains(capitalRegex) &&
                password.contains(numberRegex) &&
                password.contains(specialCharRegex)
    }

    private fun isEmailFieldEmpty(email: String): Boolean {
        return email.isBlank() || email.isEmpty()
    }

    private fun isPasswordFieldEmpty(password: String): Boolean {
        return password.isBlank() || password.isEmpty()
    }

    private fun isFirstNameFieldEmpty(firstName: String): Boolean {
        return firstName.isBlank() || firstName.isEmpty()
    }

    private fun isLastNameFieldEmpty(lastName: String): Boolean {
        return lastName.isBlank() || lastName.isEmpty()
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