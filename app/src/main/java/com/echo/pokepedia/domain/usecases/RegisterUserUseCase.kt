package com.echo.pokepedia.domain.usecases

import com.echo.pokepedia.domain.repository.AuthRepository
import com.echo.pokepedia.util.NetworkResult
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): NetworkResult<FirebaseUser?> {
        return authRepository.register(firstName, lastName, email, password)
    }
}