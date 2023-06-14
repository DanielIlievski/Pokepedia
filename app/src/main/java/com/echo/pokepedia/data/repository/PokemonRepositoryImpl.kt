package com.echo.pokepedia.data.repository

import androidx.paging.*
import com.bumptech.glide.RequestManager
import com.echo.pokepedia.R
import com.echo.pokepedia.data.database.LocalPokemonDataSource
import com.echo.pokepedia.data.mappers.toPokemonDTO
import com.echo.pokepedia.data.mappers.toPokemonDetailsDTO
import com.echo.pokepedia.data.network.RemotePokemonDataSource
import com.echo.pokepedia.data.paging.PokemonRemoteMediator
import com.echo.pokepedia.domain.pokemon.model.PokemonDTO
import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO
import com.echo.pokepedia.domain.pokemon.model.database.TeamMemberEntity
import com.echo.pokepedia.domain.pokemon.model.network.PokemonDetailsResponse
import com.echo.pokepedia.domain.pokemon.repository.PokemonRepository
import com.echo.pokepedia.util.NetworkConnectivity
import com.echo.pokepedia.util.NetworkResult
import com.echo.pokepedia.util.PAGE_SIZE
import com.echo.pokepedia.util.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val remotePokemonDataSource: RemotePokemonDataSource,
    private val localPokemonDataSource: LocalPokemonDataSource,
    private val dispatcher: CoroutineDispatcher,
    private val glide: RequestManager,
    private val networkConnectivity: NetworkConnectivity
) : PokemonRepository {

    override suspend fun getMyTeamList(): Flow<List<PokemonDTO>> {
        val localTeamMembers = localPokemonDataSource.getAllTeamMembers().map { list ->
            list.filter { it.teamMember != null }.map { it.pokemon.toPokemonDTO() }
        }
        return localTeamMembers
    }

    override suspend fun addPokemonToMyTeam(pokemonId: Int) {
        localPokemonDataSource.insertTeamMember(
            TeamMemberEntity(pokemonId)
        )
    }

    override suspend fun removePokemonFromMyTeam(pokemonId: Int) {
        localPokemonDataSource.deleteTeamMember(pokemonId)
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
                localPokemonDataSource = localPokemonDataSource,
                remotePokemonDataSource = remotePokemonDataSource,
                dispatcher = dispatcher,
                glide = glide
            )
        ).flow.map { pagingData ->
            pagingData.map { pokemonEntity -> pokemonEntity.toPokemonDTO() }
        }
    }

    override fun searchPokemonsByNameOrId(query: String): Flow<List<PokemonDTO>> {
        return localPokemonDataSource.getAllPokemonsByNameOrId(query).map { pokemonEntityList ->
            pokemonEntityList.map { it.toPokemonDTO() }
        }
    }

    override suspend fun getPokemonDetails(
        name: String
    ): NetworkResult<PokemonDetailsDTO> {

        return if (!networkConnectivity.isNetworkAvailable) {
            val localPokemonDetails = localPokemonDataSource.getPokemonDetails(name).firstOrNull()
            if (localPokemonDetails != null) {
                NetworkResult.Success(localPokemonDetails.toPokemonDetailsDTO())
            } else {
                NetworkResult.Failure(UiText.StringResource(R.string.no_internet_connection))
            }
        } else {
            val pokemonResult = remotePokemonDataSource.getPokemonInfo(name)
            when (pokemonResult) {
                is NetworkResult.Failure -> onFailedPokemonFetch(pokemonResult.exception)
                is NetworkResult.Success -> onSuccessfulPokemonFetch(pokemonResult.result)
            }
        }
    }

    private fun onFailedPokemonFetch(exception: UiText): NetworkResult.Failure {
        return NetworkResult.Failure(exception)
    }

    private suspend fun onSuccessfulPokemonFetch(result: PokemonDetailsResponse): NetworkResult<PokemonDetailsDTO> {
        val pokemonDetailsDto = result.toPokemonDetailsDTO()
        writePokemonDetailsToDatabase(pokemonDetailsDto)
        return NetworkResult.Success(result.toPokemonDetailsDTO())
    }

    private suspend fun writePokemonDetailsToDatabase(pokemonDetailsDto: PokemonDetailsDTO) {
        localPokemonDataSource.insertPokemonDetails(pokemonDetailsDto.toPokemonDetailsEntity())
        pokemonDetailsDto.toStatEntityList().forEach { statEntity ->
            localPokemonDataSource.insertStat(statEntity)
        }
    }
}
