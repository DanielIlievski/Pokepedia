package com.echo.pokepedia.data.paging

import android.graphics.Color
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.palette.graphics.Palette
import androidx.room.withTransaction
import com.bumptech.glide.RequestManager
import com.echo.pokepedia.data.database.room.PokepediaDatabase
import com.echo.pokepedia.data.network.RemotePokemonDataSource
import com.echo.pokepedia.domain.pokemon.model.database.PokemonEntity
import com.echo.pokepedia.util.NetworkResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.ExecutionException
import javax.inject.Inject
import kotlin.math.ceil

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator @Inject constructor(
    private val pokemonDb: PokepediaDatabase,
    private val remotePokemonDataSource: RemotePokemonDataSource,
    private val dispatcher: CoroutineDispatcher,
    private val glide: RequestManager
) : RemoteMediator<Int, PokemonEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonEntity>
    ): MediatorResult {
        return try {
            withContext(dispatcher) {
                val offset: Int = when (loadType) {
                    LoadType.REFRESH -> 0
                    LoadType.PREPEND -> return@withContext MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    LoadType.APPEND -> {
                        val lastItem = state.lastItemOrNull()
                        if (lastItem == null) {
                            0
                        } else {
                            ceil((lastItem.id * 1.0 / state.config.pageSize)).toInt() * state.config.pageSize
                        }
                    }
                }

                val pokemonListResponse =
                    remotePokemonDataSource.getPokemonList(state.config.pageSize, offset)

                when (pokemonListResponse) {
                    is NetworkResult.Failure -> MediatorResult.Error(
                        Exception(pokemonListResponse.exception.toString())
                    )
                    is NetworkResult.Success -> {
                        val endOfPagination = pokemonListResponse.result?.results?.isEmpty()

                        val pokemonDtos =
                            pokemonListResponse.result?.results?.map { it.toPokemonDTO() }?.map {
                                it.overrideColors(
                                    getDominantColor(it.url ?: ""),
                                    getDominantColor(it.urlShiny ?: "")
                                )
                            }

                        pokemonDb.withTransaction {
                            if (loadType == LoadType.REFRESH) {
                                pokemonDb.pokemonDao().deleteAllPokemon()
                            }
                            val pokemonEntities =
                                pokemonDtos?.map { it.toPokemonEntity() }
                            pokemonDb.pokemonDao().upsertAllPokemons(pokemonEntities!!)
                        }
                        MediatorResult.Success(endOfPaginationReached = endOfPagination!!)
                    }
                }
            }
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getDominantColor(imageUrl: String): Int {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = glide
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get()
                val palette = Palette.from(bitmap).generate()
                palette.getDominantColor(Color.WHITE)
            } catch (e: ExecutionException) {
                Color.WHITE
            } catch (e: HttpException) {
                Color.WHITE
            }
        }
    }
}

