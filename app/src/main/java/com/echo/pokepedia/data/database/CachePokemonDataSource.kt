package com.echo.pokepedia.data.database

import androidx.paging.PagingSource
import com.echo.pokepedia.domain.pokemon.model.database.PokemonDetailsEntity
import com.echo.pokepedia.domain.pokemon.model.database.PokemonEntity
import com.echo.pokepedia.domain.pokemon.model.database.StatEntity
import com.echo.pokepedia.domain.pokemon.model.database.TeamMemberEntity
import com.echo.pokepedia.domain.pokemon.model.database.relation.PokemonAndTeamMember
import com.echo.pokepedia.domain.pokemon.model.database.relation.PokemonDetailsWithStats
import kotlinx.coroutines.flow.Flow

interface CachePokemonDataSource {

    suspend fun insertPokemon(pokemon: PokemonEntity)

    suspend fun insertAllPokemons(pokemonList: List<PokemonEntity>)

    fun getAllPokemons(): PagingSource<Int, PokemonEntity>

    fun getAllPokemonsByNameOrId(query: String): Flow<List<PokemonEntity>>

    suspend fun deleteAll()

    suspend fun deleteAllAndInsertNew(pokemonList: List<PokemonEntity>)

    suspend fun insertPokemonDetails(pokemonDetails: PokemonDetailsEntity)

    fun getPokemonDetails(pokemonName: String): Flow<PokemonDetailsWithStats>

    suspend fun insertStat(stat: StatEntity)

    suspend fun insertTeamMember(teamMember: TeamMemberEntity)

    fun getAllTeamMembers(): Flow<List<PokemonAndTeamMember>>

    suspend fun deleteTeamMember(pokemonId: Int)
}