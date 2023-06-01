package com.echo.pokepedia.data.database.room.pokemon

import androidx.paging.PagingSource
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

    @Query("SELECT * FROM pokemon")
    fun getAllPokemons(): PagingSource<Int, PokemonEntity>

    @Query("DELETE FROM pokemon")
    fun deleteAllPokemon()

    @Transaction
    suspend fun deleteAllAndInsertNewPokemons(pokemonList: List<PokemonEntity>) {
        deleteAllPokemon()
        upsertAllPokemons(pokemonList)
    }

    @Upsert
    suspend fun upsertPokemonDetails(pokemonDetails: PokemonDetailsEntity)

    @Transaction
    @Query("SELECT * FROM pokemon_details WHERE name IS :pokemonName")
    fun getPokemonDetails(pokemonName: String): Flow<PokemonDetailsWithStats>
}