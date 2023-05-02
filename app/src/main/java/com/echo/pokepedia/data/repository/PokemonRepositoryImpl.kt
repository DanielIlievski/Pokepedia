package com.echo.pokepedia.data.repository

import android.graphics.Color
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.palette.graphics.Palette
import com.bumptech.glide.RequestManager
import com.echo.pokepedia.data.network.RemotePokemonDataSource
import com.echo.pokepedia.domain.pokemon.model.PokemonDTO
import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO
import com.echo.pokepedia.domain.pokemon.model.network.PokemonDetailsResponse
import com.echo.pokepedia.domain.pokemon.repository.PokemonRepository
import com.echo.pokepedia.ui.pokemon.home.PokemonPagingSource
import com.echo.pokepedia.util.NetworkResult
import com.echo.pokepedia.util.PAGE_SIZE
import com.echo.pokepedia.util.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val remotePokemonDataSource: RemotePokemonDataSource,
    private val glide: RequestManager
) : PokemonRepository {

    override suspend fun getPokemonList(): Flow<PagingData<PokemonDTO>> {
        return Pager(
            config = PagingConfig(
                PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            PokemonPagingSource(remotePokemonDataSource)
        }.flow.map {
            it.map { pokemonDTO ->
                pokemonDTO.copy(
                    dominantColor = getDominantColor(pokemonDTO.url ?: ""),
                    dominantColorShiny = getDominantColor(pokemonDTO.urlShiny ?: "")
                )
            }
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

    private fun onFailedPokemonFetch(exception: UiText?): NetworkResult.Failure {
        return NetworkResult.Failure(exception)
    }

    private fun onSuccessfulPokemonFetch(result: PokemonDetailsResponse?): NetworkResult<PokemonDetailsDTO> {
        return NetworkResult.Success(result!!.toPokemonDetailsDTO())
    }

    private suspend fun getDominantColor(imageUrl: String): Int {
        return withContext(Dispatchers.IO) {
            val bitmap = glide
                .asBitmap()
                .load(imageUrl)
                .submit()
                .get()

            val palette = Palette.from(bitmap).generate()
            palette.getDominantColor(Color.WHITE)
        }
    }
}
