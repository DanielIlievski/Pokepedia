package com.echo.pokepedia.domain.pokemon.interactors

import androidx.paging.PagingData
import com.echo.pokepedia.domain.pokemon.model.Pokemon
import com.echo.pokepedia.domain.pokemon.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPokemonListFromApiUserCase @Inject constructor(
    private val pokemonRepository: PokemonRepository
) {

    suspend operator fun invoke(): Flow<PagingData<Pokemon>> {
        return pokemonRepository.getPokemonList()
    }
}