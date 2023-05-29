package com.echo.pokepedia.data.database.room

import androidx.paging.PagingSource
import com.echo.pokepedia.data.database.LocalPokemonDataSource
import com.echo.pokepedia.domain.pokemon.model.database.PokemonDetailsEntity
import com.echo.pokepedia.domain.pokemon.model.database.PokemonEntity
import com.echo.pokepedia.domain.pokemon.model.database.StatEntity
import com.echo.pokepedia.domain.pokemon.model.database.TeamMemberEntity
import com.echo.pokepedia.domain.pokemon.model.database.relation.PokemonAndTeamMember
import com.echo.pokepedia.domain.pokemon.model.database.relation.PokemonDetailsWithStats
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomPokemonDataSource @Inject constructor(
    private val pokemonDao: PokemonDao,
    private val statDao: StatDao,
    private val teamMemberDao: TeamMemberDao
) : LocalPokemonDataSource {

    override suspend fun insertPokemon(pokemon: PokemonEntity) {
        pokemonDao.upsertPokemon(pokemon)
    }

    override suspend fun insertAllPokemons(pokemonList: List<PokemonEntity>) {
        pokemonDao.upsertAllPokemons(pokemonList)
    }

    override fun getAllPokemons(): PagingSource<Int, PokemonEntity> {
        return pokemonDao.getAllPokemons()
    }

    override suspend fun deleteAll() {
        pokemonDao.deleteAllPokemon()
    }

    override suspend fun deleteAllAndInsertNew(pokemonList: List<PokemonEntity>) {
        pokemonDao.deleteAllAndInsertNewPokemons(pokemonList)
    }

    override suspend fun insertPokemonDetails(pokemonDetails: PokemonDetailsEntity) {
        pokemonDao.upsertPokemonDetails(pokemonDetails)
    }

    override fun getPokemonDetails(pokemonName: String): Flow<PokemonDetailsWithStats> {
        return pokemonDao.getPokemonDetails(pokemonName)
    }

    override suspend fun insertStat(stat: StatEntity) {
        statDao.upsertStat(stat)
    }

    override suspend fun insertTeamMember(teamMember: TeamMemberEntity) {
        teamMemberDao.upsertTeamMember(teamMember)
    }

    override fun getAllTeamMembers(): Flow<List<PokemonAndTeamMember>> {
        return teamMemberDao.getAllTeamMembers()
    }

    override suspend fun deleteTeamMember(pokemonId: Int) {
        teamMemberDao.deleteTeamMember(pokemonId)
    }
}