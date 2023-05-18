package com.echo.pokepedia.data.database.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.echo.pokepedia.domain.pokemon.model.database.StatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatDao {

    @Upsert
    suspend fun upsertStat(stat: StatEntity)

    @Query("SELECT * FROM stat WHERE id IS :statId")
    fun getStat(statId: Int): Flow<StatEntity>

    @Query("SELECT * FROM stat WHERE pokemon_id IS :pokemonId")
    fun getStatsWithPokemonId(pokemonId: Int): Flow<List<StatEntity>>
}