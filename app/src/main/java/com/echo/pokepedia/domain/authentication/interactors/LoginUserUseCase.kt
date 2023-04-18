package com.echo.pokepedia.domain.authentication.interactors

import com.echo.pokepedia.domain.authentication.repository.AuthRepository
import com.echo.pokepedia.util.NetworkResult
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(email: String, password: String): NetworkResult<FirebaseUser?> {
        return authRepository.login(email, password)
    }
}