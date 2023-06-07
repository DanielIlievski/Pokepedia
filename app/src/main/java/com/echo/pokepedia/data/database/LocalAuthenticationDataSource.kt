package com.echo.pokepedia.data.database

import com.echo.pokepedia.domain.authentication.model.User

interface LocalAuthenticationDataSource {

    suspend fun insertUser(user: User)

    suspend fun updateUser(user: User)

    suspend fun getUser(): User

    suspend fun deleteUser()
}