package com.echo.pokepedia.domain.pokemon.repository

import androidx.paging.PagingData
import com.echo.pokepedia.domain.pokemon.model.PokemonDTO
import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO
import com.echo.pokepedia.util.NetworkResult
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {

    suspend fun getPokemonList(): Flow<PagingData<PokemonDTO>>

    suspend fun getPokemonDetails(name: String): NetworkResult<PokemonDetailsDTO>

    suspend fun getMyTeamList(): Flow<List<PokemonDTO>>

    suspend fun addPokemonToMyTeam(pokemonId: Int)

    suspend fun removePokemonFromMyTeam(pokemonId: Int)
}