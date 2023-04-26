package com.echo.pokepedia.domain.pokemon.model

data class PokemonDTO(
    val name: String? = null,
    val url: String? = null,
    var dominantColor: Int? = null
)