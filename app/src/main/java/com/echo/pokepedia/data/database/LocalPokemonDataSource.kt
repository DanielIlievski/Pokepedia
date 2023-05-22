package com.echo.pokepedia.data.database

import androidx.paging.PagingSource
import com.echo.pokepedia.domain.pokemon.model.database.PokemonDetailsEntity
import com.echo.pokepedia.domain.pokemon.model.database.PokemonEntity
import com.echo.pokepedia.domain.pokemon.model.database.StatEntity
import com.echo.pokepedia.domain.pokemon.model.database.relation.PokemonDetailsWithStats
import kotlinx.coroutines.flow.Flow

interface LocalPokemonDataSource {

    suspend fun insertPokemon(pokemon: PokemonEntity)

    suspend fun insertAllPokemons(pokemonList: List<PokemonEntity>)

    fun getPokemon(pokemonId: Int): Flow<PokemonEntity>

    fun getAllPokemons(): PagingSource<Int, PokemonEntity>

    suspend fun insertPokemonDetails(pokemonDetails: PokemonDetailsEntity)

    fun getPokemonDetails(pokemonName: String): Flow<PokemonDetailsWithStats>

    suspend fun insertStat(stat: StatEntity)

    fun getStat(statId: Int): Flow<StatEntity>

    fun getStatsWithPokemonId(pokemonId: Int): Flow<List<StatEntity>>
}