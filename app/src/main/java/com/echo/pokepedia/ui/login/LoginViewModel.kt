package com.echo.pokepedia.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echo.pokepedia.domain.usecases.GoogleSignInUserUseCase
import com.echo.pokepedia.domain.usecases.LoginUserUseCase
import com.echo.pokepedia.util.isEmailFieldEmpty
import com.echo.pokepedia.util.isPasswordFieldEmpty
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.echo.pokepedia.util.NetworkResult
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
    private val googleSignInUserUseCase: GoogleSignInUserUseCase
) : ViewModel() {

    // region viewModel variables
    private var _viewState = MutableStateFlow<LoginViewState>(LoginViewState.EmptyViewState)
    val viewState: StateFlow<LoginViewState> get() = _viewState

    private var _loginUser =
        MutableSharedFlow<NetworkResult<FirebaseUser?>>()
    val loginUser: SharedFlow<NetworkResult<FirebaseUser?>> = _loginUser

    private var _googleSignInUser = MutableSharedFlow<NetworkResult<FirebaseUser?>>()
    val googleSignInUser: SharedFlow<NetworkResult<FirebaseUser?>> = _googleSignInUser
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
                _loginUser.emit(loginUserUseCase.invoke(email, password))
            }
        }
    }

    fun googleSignIn(task: Task<GoogleSignInAccount>) = viewModelScope.launch {
        _googleSignInUser.emit(googleSignInUserUseCase.invoke(task))
    }

}

sealed class LoginViewState {
    object EmptyViewState : LoginViewState()
    object EmptyPasswordField : LoginViewState()
    object EmptyEmailField : LoginViewState()
}