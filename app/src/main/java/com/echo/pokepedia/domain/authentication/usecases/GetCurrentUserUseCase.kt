package com.echo.pokepedia.domain.authentication.usecases

import com.echo.pokepedia.domain.authentication.repository.AuthRepository
import com.echo.pokepedia.util.NetworkResult
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(): NetworkResult<FirebaseUser?> {
        return authRepository.getCurrentUser()
    }
}