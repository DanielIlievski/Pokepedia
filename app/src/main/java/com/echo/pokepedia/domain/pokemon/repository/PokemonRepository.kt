package com.echo.pokepedia.domain.pokemon.repository

import androidx.paging.PagingData
import com.echo.pokepedia.domain.pokemon.model.Pokemon
import com.echo.pokepedia.domain.pokemon.model.PokemonDetails
import com.echo.pokepedia.domain.pokemon.model.PokemonList
import com.echo.pokepedia.util.NetworkResult
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {

    suspend fun getPokemonListFromApi(limit: Int, offset: Int): NetworkResult<PokemonList>

    suspend fun getPokemonList(): Flow<PagingData<Pokemon>>

    suspend fun getPokemonInfoFromApi(name: String): NetworkResult<PokemonDetails>
}