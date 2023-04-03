package com.echo.pokepedia.domain.repository

import com.echo.pokepedia.util.Resource
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {

    suspend fun getCurrentUser(): Resource<FirebaseUser?>

    suspend fun login(email: String, password: String): Resource<FirebaseUser>

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String): Resource<FirebaseUser>

    suspend fun logout()
}