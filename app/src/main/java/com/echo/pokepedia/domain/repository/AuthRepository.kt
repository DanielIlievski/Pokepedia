package com.echo.pokepedia.domain.repository

import com.echo.pokepedia.util.NetworkResult
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {

    suspend fun getCurrentUser(): NetworkResult<FirebaseUser?>

    suspend fun login(email: String, password: String): NetworkResult<FirebaseUser>

    suspend fun googleSignIn(task: Task<GoogleSignInAccount>): NetworkResult<FirebaseUser?>

    suspend fun facebookSignIn(token: AccessToken): NetworkResult<FirebaseUser?>

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): NetworkResult<FirebaseUser>

    suspend fun logout(): NetworkResult<Boolean>
}