package com.echo.pokepedia.data.database.room

import androidx.room.Dao
import androidx.room.Upsert
import com.echo.pokepedia.domain.pokemon.model.database.StatEntity

@Dao
interface StatDao {

    @Upsert
    suspend fun upsertStat(stat: StatEntity)
}