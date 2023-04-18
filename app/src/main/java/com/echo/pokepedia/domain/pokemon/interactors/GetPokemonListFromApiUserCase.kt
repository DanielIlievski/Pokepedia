package com.echo.pokepedia.domain.pokemon.interactors

import com.echo.pokepedia.domain.pokemon.PokemonList
import com.echo.pokepedia.domain.pokemon.repository.PokemonRepository
import com.echo.pokepedia.util.NetworkResult
import javax.inject.Inject

class GetPokemonListFromApiUserCase @Inject constructor(
    private val pokemonRepository: PokemonRepository
) {

    suspend operator fun invoke(limit: Int, offset: Int): NetworkResult<PokemonList> {
        return pokemonRepository.getPokemonListFromApi(limit, offset)
    }
}