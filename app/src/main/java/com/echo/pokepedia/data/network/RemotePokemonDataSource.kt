package com.echo.pokepedia.data.network

import com.echo.pokepedia.domain.pokemon.model.network.PokemonDetailsDTO
import com.echo.pokepedia.domain.pokemon.model.network.PokemonListDTO
import com.echo.pokepedia.util.NetworkResult

interface RemotePokemonDataSource {

    suspend fun getPokemonList(limit: Int, offset: Int): NetworkResult<PokemonListDTO?>

    suspend fun getPokemonInfo(name: String): NetworkResult<PokemonDetailsDTO?>
}