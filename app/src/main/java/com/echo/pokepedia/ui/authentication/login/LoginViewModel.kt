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

    private val _signInUser = MutableSharedFlow<NetworkResult<FirebaseUser?>>()
    val signInUser: SharedFlow<NetworkResult<FirebaseUser?>> = _signInUser

    private val _resetPassword = MutableSharedFlow<NetworkResult<Boolean>>()
    val resetPassword: SharedFlow<NetworkResult<Boolean>> = _resetPassword
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
                _signInUser.emit(loginUserUseCase.invoke(email, password))
            }
        }
    }

    fun googleSignIn(task: Task<GoogleSignInAccount>) = viewModelScope.launch {
        _signInUser.emit(googleSignInUseCase.invoke(task))
    }

    fun facebookSignIn(token: AccessToken) = viewModelScope.launch {
        _signInUser.emit(facebookSignInUseCase.invoke(token))
    }

    fun resetPassword(email: String) = viewModelScope.launch {
        _resetPassword.emit(resetPasswordUseCase.invoke(email))
    }

    fun isUserAuthenticated(): Boolean {
        return isUserAuthenticatedUseCase.invoke()
    }

}

sealed class LoginViewState {
    object EmptyViewState : LoginViewState()
    object EmptyPasswordField : LoginViewState()
    object EmptyEmailField : LoginViewState()
}