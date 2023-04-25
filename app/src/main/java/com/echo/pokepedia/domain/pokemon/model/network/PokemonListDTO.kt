package com.echo.pokepedia.domain.pokemon.model.network

import com.echo.pokepedia.domain.pokemon.PokemonList

data class PokemonListDTO(
    val count: Int,
    val results: List<Result>
) {
    fun toPokemonList(): PokemonList {
        val name = results.map { it.name }
        val url = results.map { it.url }

        return PokemonList(
            count,
            name,
            url
        )
    }
}