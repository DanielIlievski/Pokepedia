package com.echo.pokepedia.data.network.retrofit

import com.echo.pokepedia.R
import com.echo.pokepedia.data.network.RemotePokemonDataSource
import com.echo.pokepedia.domain.pokemon.model.network.PokemonDetailsResponse
import com.echo.pokepedia.domain.pokemon.model.network.PokemonListResponse
import com.echo.pokepedia.util.NetworkResult
import com.echo.pokepedia.util.UiText
import javax.inject.Inject

class RetrofitPokemonDataSource @Inject constructor(
    private val pokemonDbApi: PokemonDbApi
) : RemotePokemonDataSource {

    override suspend fun getPokemonList(
        limit: Int,
        offset: Int
    ): NetworkResult<PokemonListResponse> {
        return try {
            val pokemonList = pokemonDbApi.getPokemonList(limit, offset)
            if (pokemonList.isSuccessful && pokemonList.body() != null) {
                NetworkResult.Success(pokemonList.body()!!)
            } else {
                NetworkResult.Failure(UiText.StringResource(R.string.fetch_unsuccessful))
            }
        } catch (e: Exception) {
            NetworkResult.Failure(UiText.DynamicString(e.localizedMessage))
        }
    }

    override suspend fun getPokemonInfo(name: String): NetworkResult<PokemonDetailsResponse> {
        return try {
            val pokemon = pokemonDbApi.getPokemonInfo(name)

            if (pokemon.isSuccessful) {
                if (pokemon.body() != null) {
                    NetworkResult.Success(pokemon.body()!!)
                } else {
                    NetworkResult.Failure(UiText.StringResource(R.string.pokemon_details_empty))
                }
            } else {
                NetworkResult.Failure(UiText.StringResource(R.string.fetch_unsuccessful))
            }
        } catch (e: Exception) {
            NetworkResult.Failure(UiText.DynamicString(e.localizedMessage))
        }
    }

}