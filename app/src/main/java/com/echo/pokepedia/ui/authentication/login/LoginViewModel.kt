package com.echo.pokepedia.ui.authentication.login

import androidx.lifecycle.viewModelScope
import com.echo.pokepedia.domain.authentication.interactors.*
import com.echo.pokepedia.ui.BaseViewModel
import com.echo.pokepedia.util.NetworkResult
import com.echo.pokepedia.util.isEmailFieldEmpty
import com.echo.pokepedia.util.isPasswordFieldEmpty
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
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
    private val loginUserUseCase: LoginUserUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val facebookSignInUseCase: FacebookSignInUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val isUserAuthenticatedUseCase: IsUserAuthenticatedUseCase
) : BaseViewModel() {

    // region viewModel variables
    private val _viewState = MutableStateFlow<LoginViewState>(LoginViewState.EmptyViewState)
    val viewState: StateFlow<LoginViewState> get() = _viewState

    private val _signInUser = MutableSharedFlow<FirebaseUser?>()
    val signInUser: SharedFlow<FirebaseUser?> = _signInUser

    private val _resetPassword = MutableSharedFlow<Boolean>()
    val resetPassword: SharedFlow<Boolean> = _resetPassword
    // endregion

    fun login(email: String, password: String) {
        if (isEmailFieldEmpty(email)) {
            _viewState.value = LoginViewState.EmptyEmailField
        }
        if (isPasswordFieldEmpty(password)) {
            _viewState.value = LoginViewState.EmptyPasswordField
        }
        if (!isEmailFieldEmpty(email) && !isPasswordFieldEmpty(password)) {
            viewModelScope.launch {
                _viewState.value = LoginViewState.LoadingState
                val loginUserResponse = loginUserUseCase.invoke(email, password)
                handleUseCaseResponse(loginUserResponse)
            }
        }
    }

    fun googleSignIn(task: Task<GoogleSignInAccount>) = viewModelScope.launch {
        _viewState.value = LoginViewState.LoadingState
        val googleSignInResponse = googleSignInUseCase.invoke(task)
        handleUseCaseResponse(googleSignInResponse)
    }

    fun facebookSignIn(token: AccessToken) = viewModelScope.launch {
        _viewState.value = LoginViewState.LoadingState
        val facebookSignInResponse = facebookSignInUseCase.invoke(token)
        handleUseCaseResponse(facebookSignInResponse)
    }

    fun resetPassword(email: String) = viewModelScope.launch {
        val resetPasswordResponse = resetPasswordUseCase.invoke(email)
        when (resetPasswordResponse) {
            is NetworkResult.Success -> _resetPassword.emit(resetPasswordResponse.result)
            is NetworkResult.Failure -> _errorObservable.emit(resetPasswordResponse.exception)
        }
    }

    fun isUserAuthenticated(): Boolean {
        return isUserAuthenticatedUseCase.invoke()
    }

    private suspend fun handleUseCaseResponse(response: NetworkResult<FirebaseUser?>) {
        when (response) {
            is NetworkResult.Success -> {
                _viewState.value = LoginViewState.EmptyViewState
                _signInUser.emit(response.result)
            }
            is NetworkResult.Failure -> {
                _viewState.value = LoginViewState.EmptyViewState
                _errorObservable.emit(response.exception)
            }
        }
    }
}

sealed class LoginViewState {
    object EmptyViewState : LoginViewState()
    object EmptyPasswordField : LoginViewState()
    object EmptyEmailField : LoginViewState()
    object LoadingState : LoginViewState()
}