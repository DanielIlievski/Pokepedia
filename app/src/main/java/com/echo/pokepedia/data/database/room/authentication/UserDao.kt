package com.echo.pokepedia.data.database.room.authentication

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.echo.pokepedia.domain.authentication.model.database.UserEntity

@Dao
interface UserDao {

    @Upsert
    suspend fun upsertUser(user: UserEntity)

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUser(): UserEntity

    @Query("DELETE FROM user")
    suspend fun deleteUser()
}