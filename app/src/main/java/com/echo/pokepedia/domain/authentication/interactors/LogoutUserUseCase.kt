package com.echo.pokepedia.domain.authentication.interactors

import com.echo.pokepedia.domain.authentication.repository.AuthRepository
import com.echo.pokepedia.util.NetworkResult
import javax.inject.Inject

class LogoutUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(): NetworkResult<Boolean> {
        return authRepository.logout()
    }
}