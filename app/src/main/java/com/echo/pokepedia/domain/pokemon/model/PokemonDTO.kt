package com.echo.pokepedia.domain.pokemon.model

data class PokemonDTO(
    val id: Int? = null,
    val name: String? = null,
    val url: String? = null,
    val urlShiny: String? = null,
    var dominantColor: Int? = null,
    var dominantColorShiny: Int? = null
)