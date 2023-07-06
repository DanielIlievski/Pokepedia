package com.echo.pokepedia.domain.pokemon.interactors

import com.echo.pokepedia.domain.pokemon.repository.PokemonRepository
import javax.inject.Inject

class AddPokemonToMyTeamUseCase @Inject constructor(
    private val pokemonRepository: PokemonRepository
) {

    suspend operator fun invoke(pokemonId: Int) {
        pokemonRepository.addPokemonToMyTeam(pokemonId)
    }
}