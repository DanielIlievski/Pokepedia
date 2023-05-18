package com.echo.pokepedia.data.database.room

import androidx.room.*
import com.echo.pokepedia.domain.pokemon.model.database.PokemonDetailsEntity
import com.echo.pokepedia.domain.pokemon.model.database.PokemonEntity
import com.echo.pokepedia.domain.pokemon.model.database.relation.PokemonDetailsWithStats
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    @Upsert
    suspend fun upsertPokemon(pokemon: PokemonEntity)

    @Upsert
    suspend fun upsertAllPokemons(pokemonList: List<PokemonEntity>)

    @Query("SELECT * FROM pokemon WHERE id IS :pokemonId")
    fun getPokemon(pokemonId: Int): Flow<PokemonEntity>

    @Query("SELECT * FROM pokemon")
    fun getAllPokemons(): Flow<List<PokemonEntity>>

    @Upsert
    suspend fun upsertPokemonDetails(pokemonDetails: PokemonDetailsEntity)

    @Transaction
    @Query("SELECT * FROM pokemon_details WHERE id IS :pokemonId")
    fun getPokemonDetails(pokemonId: Int): Flow<PokemonDetailsWithStats>
}