package com.echo.pokepedia.domain.pokemon.interactors

import com.echo.pokepedia.domain.pokemon.model.PokemonDTO
import com.echo.pokepedia.domain.pokemon.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchPokemonsByNameOrIdUseCase @Inject constructor(
    private val pokemonRepository: PokemonRepository
) {

    operator fun invoke(query: String): Flow<List<PokemonDTO>> {
        return pokemonRepository.searchPokemonsByNameOrId(query)
    }
}