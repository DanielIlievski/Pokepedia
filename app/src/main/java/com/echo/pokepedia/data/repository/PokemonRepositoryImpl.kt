package com.echo.pokepedia.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.echo.pokepedia.data.network.RemotePokemonDataSource
import com.echo.pokepedia.domain.pokemon.model.PokemonDTO
import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO
import com.echo.pokepedia.domain.pokemon.model.PokemonListDTO
import com.echo.pokepedia.domain.pokemon.model.network.PokemonDetailsResponse
import com.echo.pokepedia.domain.pokemon.model.network.PokemonListResponse
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

    override suspend fun getPokemonList(): Flow<PagingData<PokemonDTO>> {
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