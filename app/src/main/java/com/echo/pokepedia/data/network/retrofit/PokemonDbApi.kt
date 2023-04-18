package com.echo.pokepedia.data.network.retrofit

import com.echo.pokepedia.domain.pokemon.model.network.PokemonDTO
import com.echo.pokepedia.domain.pokemon.model.network.PokemonListDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonDbApi {

    @GET("pokemon/")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<PokemonListDTO>

    @GET("pokemon/{name}")
    suspend fun getPokemonInfo(
        @Path("name") name: String
    ): Response<PokemonDTO>
}