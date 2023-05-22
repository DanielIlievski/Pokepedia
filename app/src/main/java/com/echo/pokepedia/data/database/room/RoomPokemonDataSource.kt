package com.echo.pokepedia.data.database.room

import androidx.paging.PagingSource
import com.echo.pokepedia.data.database.LocalPokemonDataSource
import com.echo.pokepedia.domain.pokemon.model.database.PokemonDetailsEntity
import com.echo.pokepedia.domain.pokemon.model.database.PokemonEntity
import com.echo.pokepedia.domain.pokemon.model.database.StatEntity
import com.echo.pokepedia.domain.pokemon.model.database.relation.PokemonDetailsWithStats
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomPokemonDataSource @Inject constructor(
    private val pokemonDao: PokemonDao,
    private val statDao: StatDao
) : LocalPokemonDataSource {

    override suspend fun insertPokemon(pokemon: PokemonEntity) {
        pokemonDao.upsertPokemon(pokemon)
    }

    override suspend fun insertAllPokemons(pokemonList: List<PokemonEntity>) {
        pokemonDao.upsertAllPokemons(pokemonList)
    }

    override fun getPokemon(pokemonId: Int): Flow<PokemonEntity> {
        return pokemonDao.getPokemon(pokemonId)
    }

    override fun getAllPokemons(): PagingSource<Int, PokemonEntity> {
        return pokemonDao.getAllPokemons()
    }

    override suspend fun insertPokemonDetails(pokemonDetails: PokemonDetailsEntity) {
        pokemonDao.upsertPokemonDetails(pokemonDetails)
    }

    override fun getPokemonDetails(pokemonId: Int): Flow<PokemonDetailsWithStats> {
        return pokemonDao.getPokemonDetails(pokemonId)
    }

    override suspend fun insertStat(stat: StatEntity) {
        statDao.upsertStat(stat)
    }

    override fun getStat(statId: Int): Flow<StatEntity> {
        return statDao.getStat(statId)
    }

    override fun getStatsWithPokemonId(pokemonId: Int): Flow<List<StatEntity>> {
        return statDao.getStatsWithPokemonId(pokemonId)
    }
}