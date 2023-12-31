package com.echo.pokepedia.domain.authentication.repository

import android.net.Uri
import com.echo.pokepedia.domain.authentication.model.User
import com.echo.pokepedia.util.NetworkResult
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun getCurrentUser(): NetworkResult<Flow<User>>

    fun isUserAuthenticated(): Boolean

    suspend fun updateUserProfilePhoto(imgUri: Uri?): NetworkResult<Boolean>

    suspend fun login(email: String, password: String): NetworkResult<FirebaseUser>

    suspend fun googleSignIn(task: Task<GoogleSignInAccount>): NetworkResult<FirebaseUser?>

    suspend fun facebookSignIn(token: AccessToken): NetworkResult<FirebaseUser?>

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): NetworkResult<FirebaseUser>

    suspend fun sendPasswordResetEmail(email: String): NetworkResult<Boolean>

    suspend fun logout(): NetworkResult<Boolean>
}