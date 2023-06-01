package com.echo.pokepedia.domain.authentication.interactors

import com.echo.pokepedia.domain.authentication.model.User
import com.echo.pokepedia.domain.authentication.repository.AuthRepository
import com.echo.pokepedia.util.NetworkResult
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(): NetworkResult<User> {
        return authRepository.getCurrentUser()
    }
}