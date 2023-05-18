package com.echo.pokepedia.domain.pokemon.interactors

import com.echo.pokepedia.domain.pokemon.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMyTeamListUseCase @Inject constructor(
    private val pokemonRepository: PokemonRepository
) {

    operator fun invoke(): Flow<List<Pair<String, Int>>> {
        return pokemonRepository.getMyTeamList()
    }
}