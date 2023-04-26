package com.echo.pokepedia.domain.pokemon.repository

import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO
import com.echo.pokepedia.domain.pokemon.model.PokemonListDTO
import com.echo.pokepedia.util.NetworkResult

interface PokemonRepository {

    suspend fun getPokemonListFromApi(limit: Int, offset: Int): NetworkResult<PokemonListDTO>

    suspend fun getPokemonInfoFromApi(name: String): NetworkResult<PokemonDetailsDTO>
}