package com.echo.pokepedia.domain.pokemon.model.network

import com.echo.pokepedia.domain.pokemon.model.PokemonList

data class PokemonListDTO(
    val count: Int,
    val results: List<PokemonDTO>
) {
    fun toPokemonList(): PokemonList {

        val pokemons = results.map {
            it.toPokemon()
        }

        return PokemonList(
            count = count,
            pokemonList = pokemons
        )
    }
}