package com.echo.pokepedia.domain.pokemon.repository

import androidx.paging.PagingData
import com.echo.pokepedia.domain.pokemon.model.PokemonDTO
import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO
import com.echo.pokepedia.util.NetworkResult
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {

    suspend fun getPokemonList(): Flow<PagingData<PokemonDTO>>

    suspend fun getPokemonInfoFromApi(name: String): NetworkResult<PokemonDetailsDTO>

    fun getMyTeamList(): Flow<List<Pair<String, Int>>>

    fun addPokemonToMyTeam(imgUrl: String, dominantColor: Int)

    fun removePokemonFromMyTeam(imgUrl: String)
}