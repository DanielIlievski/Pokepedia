package com.echo.pokepedia.domain.authentication.interactors

import com.echo.pokepedia.domain.authentication.repository.AuthRepository
import com.echo.pokepedia.util.NetworkResult
import com.facebook.AccessToken
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class FacebookSignInUserCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(token: AccessToken): NetworkResult<FirebaseUser?> {
        return authRepository.facebookSignIn(token)
    }
}