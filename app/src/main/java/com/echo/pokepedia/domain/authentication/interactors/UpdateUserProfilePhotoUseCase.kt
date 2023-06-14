package com.echo.pokepedia.domain.authentication.interactors

import android.net.Uri
import com.echo.pokepedia.domain.authentication.repository.AuthRepository
import com.echo.pokepedia.util.NetworkResult
import javax.inject.Inject

class UpdateUserProfilePhotoUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(imgUri: Uri?): NetworkResult<Boolean> {
        return authRepository.updateUserProfilePhoto(imgUri)
    }
}