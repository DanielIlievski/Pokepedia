package com.echo.pokepedia.data.database.room.authentication

import com.echo.pokepedia.data.database.LocalAuthenticationDataSource
import com.echo.pokepedia.data.mappers.toUser
import com.echo.pokepedia.data.mappers.toUserEntity
import com.echo.pokepedia.domain.authentication.model.User
import javax.inject.Inject

class LocalAuthenticationDataSourceImpl @Inject constructor(
    private val userDao: UserDao
) : LocalAuthenticationDataSource {

    override suspend fun insertUser(user: User) {
        userDao.upsertUser(user.toUserEntity())
    }

    override suspend fun getUser(): User {
        return userDao.getUser().toUser()
    }

    override suspend fun deleteUser() {
        userDao.deleteUser()
    }
}