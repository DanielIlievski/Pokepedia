package com.echo.pokepedia.data.network

import com.echo.pokepedia.domain.pokemon.model.network.PokemonDetailsResponse
import com.echo.pokepedia.domain.pokemon.model.network.PokemonListResponse
import com.echo.pokepedia.util.NetworkResult

interface RemotePokemonDataSource {

    suspend fun getPokemonList(limit: Int, offset: Int): NetworkResult<PokemonListResponse>

    suspend fun getPokemonInfo(name: String): NetworkResult<PokemonDetailsResponse>
}