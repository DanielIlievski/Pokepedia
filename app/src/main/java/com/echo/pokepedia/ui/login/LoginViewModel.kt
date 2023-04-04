package com.echo.pokepedia.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echo.pokepedia.domain.usecases.GoogleSignInUserUseCase
import com.echo.pokepedia.domain.usecases.LoginUserUseCase
import com.echo.pokepedia.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
        MutableSharedFlow<Resource<FirebaseUser?>>()
    val loginUser: SharedFlow<Resource<FirebaseUser?>> = _loginUser

    private var _googleSignInUser = MutableSharedFlow<Resource<FirebaseUser?>>()
    val googleSignInUser: SharedFlow<Resource<FirebaseUser?>> = _googleSignInUser
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

    // region auxiliary methods
    private fun isEmailFieldEmpty(email: String): Boolean {
        return email.isBlank() || email.isEmpty()
    }

    private fun isPasswordFieldEmpty(password: String): Boolean {
        return password.isBlank() || password.isEmpty()
    }
    // endregion
}

sealed class LoginViewState {
    object EmptyViewState : LoginViewState()
    object EmptyPasswordField : LoginViewState()
    object EmptyEmailField : LoginViewState()
}