package com.echo.pokepedia.domain.pokemon.interactors

import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO
import com.echo.pokepedia.domain.pokemon.repository.PokemonRepository
import com.echo.pokepedia.util.NetworkResult
import javax.inject.Inject

class GetPokemonInfoFromApiUserCase @Inject constructor(
    private val pokemonRepository: PokemonRepository
) {

    suspend operator fun invoke(name: String): NetworkResult<PokemonDetailsDTO> {
        return pokemonRepository.getPokemonInfoFromApi(name)
    }
}