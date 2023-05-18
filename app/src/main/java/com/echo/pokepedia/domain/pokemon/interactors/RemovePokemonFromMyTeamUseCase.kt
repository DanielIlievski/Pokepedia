package com.echo.pokepedia.domain.pokemon.interactors

import com.echo.pokepedia.domain.pokemon.repository.PokemonRepository
import javax.inject.Inject

class RemovePokemonFromMyTeamUseCase @Inject constructor(
    private val pokemonRepository: PokemonRepository
) {

    operator fun invoke(imgUrl: String) {
        pokemonRepository.removePokemonFromMyTeam(imgUrl)
    }
}