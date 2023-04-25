package com.echo.pokepedia.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.echo.pokepedia.data.network.RemotePokemonDataSource
import com.echo.pokepedia.domain.pokemon.model.Pokemon
import com.echo.pokepedia.domain.pokemon.model.PokemonDetails
import com.echo.pokepedia.domain.pokemon.model.PokemonList
import com.echo.pokepedia.domain.pokemon.model.network.PokemonDetailsDTO
import com.echo.pokepedia.domain.pokemon.model.network.PokemonListDTO
import com.echo.pokepedia.domain.pokemon.repository.PokemonRepository
import com.echo.pokepedia.ui.pokemon.home.PokemonPagingSource
import com.echo.pokepedia.util.NetworkResult
import com.echo.pokepedia.util.PAGE_SIZE
import com.echo.pokepedia.util.UiText
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val remotePokemonDataSource: RemotePokemonDataSource
) : PokemonRepository {

    override suspend fun getPokemonListFromApi(
        limit: Int,
        offset: Int
    ): NetworkResult<PokemonList> {
        val pokemonListResult = remotePokemonDataSource.getPokemonList(limit, offset)

        return when (pokemonListResult) {
            is NetworkResult.Failure -> onFailedPokemonListFetch(pokemonListResult.exception)
            is NetworkResult.Success -> onSuccessfulPokemonListFetch(pokemonListResult.result)
        }
    }

    override suspend fun getPokemonList(): Flow<PagingData<Pokemon>> {
        return Pager(
            config = PagingConfig(
                PAGE_SIZE
            )
        ) {
            PokemonPagingSource(remotePokemonDataSource)
        }.flow
    }

    override suspend fun getPokemonInfoFromApi(
        name: String
    ): NetworkResult<PokemonDetails> {
        val pokemonResult = remotePokemonDataSource.getPokemonInfo(name)

        return when (pokemonResult) {
            is NetworkResult.Failure -> onFailedPokemonFetch(pokemonResult.exception)
            is NetworkResult.Success -> onSuccessfulPokemonFetch(pokemonResult.result)
        }
    }

    private fun onFailedPokemonListFetch(exception: UiText?): NetworkResult.Failure {
        return NetworkResult.Failure(exception)
    }

    private fun onSuccessfulPokemonListFetch(results: PokemonListDTO?): NetworkResult<PokemonList> {
        return NetworkResult.Success(results!!.toPokemonList())
    }

    private fun onFailedPokemonFetch(exception: UiText?): NetworkResult.Failure {
        return NetworkResult.Failure(exception)
    }

    private fun onSuccessfulPokemonFetch(result: PokemonDetailsDTO?): NetworkResult<PokemonDetails> {
        return NetworkResult.Success(result!!.toPokemon())
    }
}