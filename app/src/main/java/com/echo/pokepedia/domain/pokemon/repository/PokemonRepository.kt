package com.echo.pokepedia.domain.pokemon.repository

import com.echo.pokepedia.domain.pokemon.model.PokemonDetails
import com.echo.pokepedia.domain.pokemon.model.PokemonList
import com.echo.pokepedia.util.NetworkResult

interface PokemonRepository {

    suspend fun getPokemonListFromApi(limit: Int, offset: Int): NetworkResult<PokemonList>

    suspend fun getPokemonInfoFromApi(name: String): NetworkResult<PokemonDetails>
}