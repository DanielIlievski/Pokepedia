package com.echo.pokepedia.data.database

import com.echo.pokepedia.domain.authentication.model.User
import kotlinx.coroutines.flow.Flow

interface CacheUserDataSource {

    suspend fun insertUser(user: User)

    fun getUser(): Flow<User>

    suspend fun updateProfilePhoto(user: User)

    suspend fun deleteUser()
}