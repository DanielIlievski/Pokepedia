package com.echo.pokepedia.domain.authentication.interactors

import com.echo.pokepedia.domain.authentication.repository.AuthRepository
import javax.inject.Inject

class IsUserAuthenticatedUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    operator fun invoke(): Boolean {
        return authRepository.isUserAuthenticated()
    }
}