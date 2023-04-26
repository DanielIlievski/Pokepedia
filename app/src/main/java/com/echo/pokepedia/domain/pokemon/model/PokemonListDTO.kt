package com.echo.pokepedia.domain.pokemon.model

data class PokemonListDTO(
    val count: Int? = null,
    val pokemonList : List<PokemonDTO>? = null
)