package com.echo.pokepedia.domain.usecases

import com.echo.pokepedia.domain.repository.AuthRepository
import com.echo.pokepedia.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class GoogleSignInUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(task: Task<GoogleSignInAccount>): Resource<FirebaseUser?> {
        return authRepository.googleSignIn(task)
    }
}