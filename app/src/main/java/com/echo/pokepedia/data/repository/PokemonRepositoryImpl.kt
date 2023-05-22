package com.echo.pokepedia.data.repository

import androidx.paging.*
import com.bumptech.glide.RequestManager
import com.echo.pokepedia.data.database.LocalPokemonDataSource
import com.echo.pokepedia.data.database.room.PokepediaDatabase
import com.echo.pokepedia.data.network.RemotePokemonDataSource
import com.echo.pokepedia.data.paging.PokemonRemoteMediator
import com.echo.pokepedia.domain.pokemon.model.PokemonDTO
import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO
import com.echo.pokepedia.domain.pokemon.model.network.PokemonDetailsResponse
import com.echo.pokepedia.domain.pokemon.repository.PokemonRepository
import com.echo.pokepedia.util.NetworkResult
import com.echo.pokepedia.util.PAGE_SIZE
import com.echo.pokepedia.util.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val remotePokemonDataSource: RemotePokemonDataSource,
    private val localPokemonDataSource: LocalPokemonDataSource,
    private val pokemonDb: PokepediaDatabase,
    private val dispatcher: CoroutineDispatcher,
    private val glide: RequestManager
) : PokemonRepository {

    private val _myTeamListFlow = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    private val myTeamListFlow: Flow<List<Pair<String, Int>>> = _myTeamListFlow

    override fun getMyTeamList(): Flow<List<Pair<String, Int>>> {
        return myTeamListFlow
    }

    override fun addPokemonToMyTeam(imgUrl: String, dominantColor: Int) {
        val myTeamList = _myTeamListFlow.value.toMutableList()
        myTeamList.add(imgUrl to dominantColor)
        _myTeamListFlow.value = myTeamList
    }

    override fun removePokemonFromMyTeam(imgUrl: String) {
        val myTeamList = _myTeamListFlow.value.toMutableList()
        val updatedList = myTeamList.filterNot { it.first == imgUrl }
        _myTeamListFlow.value = updatedList
    }

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getPokemonList(): Flow<PagingData<PokemonDTO>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE
            ),
            pagingSourceFactory = {
                localPokemonDataSource.getAllPokemons()
            },
            remoteMediator = PokemonRemoteMediator(
                pokemonDb = pokemonDb,
                remotePokemonDataSource = remotePokemonDataSource,
                dispatcher = dispatcher,
                glide = glide
            )
        ).flow.map {pagingData ->
            pagingData.map { pokemonEntity -> pokemonEntity.toPokemonDTO() }
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
}
