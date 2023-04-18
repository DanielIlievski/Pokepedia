package com.echo.pokepedia.domain.pokemon.repository

import com.echo.pokepedia.domain.pokemon.Pokemon
import com.echo.pokepedia.domain.pokemon.PokemonList
import com.echo.pokepedia.util.NetworkResult

interface PokemonRepository {

    suspend fun getPokemonListFromApi(limit: Int, offset: Int): NetworkResult<PokemonList>

    suspend fun getPokemonInfoFromApi(name: String): NetworkResult<Pokemon>
}