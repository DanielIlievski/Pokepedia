package com.echo.pokepedia.domain.authentication.interactors

import com.echo.pokepedia.domain.authentication.repository.AuthRepository
import com.echo.pokepedia.util.NetworkResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class GoogleSignInUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(task: Task<GoogleSignInAccount>): NetworkResult<FirebaseUser?> {
        return authRepository.googleSignIn(task)
    }
}