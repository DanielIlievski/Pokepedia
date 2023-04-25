package com.echo.pokepedia.domain.pokemon

data class PokemonList(
    val count: Int? = null,
    val name: List<String>? = null,
    val url: List<String>? = null
)