package com.echo.pokepedia.data.repository

import com.echo.pokepedia.data.network.RemotePokemonDataSource
import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO
import com.echo.pokepedia.domain.pokemon.model.PokemonListDTO
import com.echo.pokepedia.domain.pokemon.model.network.PokemonDetailsResponse
import com.echo.pokepedia.domain.pokemon.model.network.PokemonListResponse
import com.echo.pokepedia.domain.pokemon.repository.PokemonRepository
import com.echo.pokepedia.util.NetworkResult
import com.echo.pokepedia.util.UiText
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val remotePokemonDataSource: RemotePokemonDataSource
) : PokemonRepository {

    override suspend fun getPokemonListFromApi(
        limit: Int,
        offset: Int
    ): NetworkResult<PokemonListDTO> {
        val pokemonListResult = remotePokemonDataSource.getPokemonList(limit, offset)

        return when (pokemonListResult) {
            is NetworkResult.Failure -> onFailedPokemonListFetch(pokemonListResult.exception)
            is NetworkResult.Success -> onSuccessfulPokemonListFetch(pokemonListResult.result)
        }
    }

    override suspend fun getPokemonInfoFromApi(
        name: String
    ): NetworkResult<PokemonDetailsDTO> {
        val pokemonResult = remotePokemonDataSource.getPokemonInfo(name)

        return when (pokemonResult) {
            is NetworkResult.Failure -> onFailedPokemonFetch(pokemonResult.exception)
            is NetworkResult.Success -> onSuccessfulPokemonFetch(pokemonResult.result)
        }
    }

    private fun onFailedPokemonListFetch(exception: UiText?): NetworkResult.Failure {
        return NetworkResult.Failure(exception)
    }

    private fun onSuccessfulPokemonListFetch(results: PokemonListResponse?): NetworkResult<PokemonListDTO> {
        return NetworkResult.Success(results!!.toPokemonListDTO())
    }

    private fun onFailedPokemonFetch(exception: UiText?): NetworkResult.Failure {
        return NetworkResult.Failure(exception)
    }

    private fun onSuccessfulPokemonFetch(result: PokemonDetailsResponse?): NetworkResult<PokemonDetailsDTO> {
        return NetworkResult.Success(result!!.toPokemonDetailsDTO())
    }
}