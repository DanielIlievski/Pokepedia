package com.echo.pokepedia.domain.pokemon.model.network

import com.echo.pokepedia.domain.pokemon.model.PokemonListDTO

data class PokemonListResponse(
    val count: Int,
    val results: List<PokemonResponse>
) {
    fun toPokemonListDTO(): PokemonListDTO {

        val pokemons = results.map {
            it.toPokemonDTO()
        }

        return PokemonListDTO(
            count = count,
            pokemonList = pokemons
        )
    }
}