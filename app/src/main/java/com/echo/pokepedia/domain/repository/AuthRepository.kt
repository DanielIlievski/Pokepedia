package com.echo.pokepedia.domain.repository

import com.echo.pokepedia.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {

    suspend fun getCurrentUser(): Resource<FirebaseUser?>

    suspend fun login(email: String, password: String): Resource<FirebaseUser>

    suspend fun googleSignIn(task: Task<GoogleSignInAccount>): Resource<FirebaseUser?>

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Resource<FirebaseUser>

    suspend fun logout(): Resource<Boolean>
}