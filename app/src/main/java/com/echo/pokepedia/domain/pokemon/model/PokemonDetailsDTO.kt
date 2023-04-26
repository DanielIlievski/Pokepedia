package com.echo.pokepedia.domain.pokemon.model

data class PokemonDetailsDTO(
    val id: Int? = null,
    val name: String? = null,
    val types: List<String>? = null,
    val abilities: List<String>? = null,
    val moves: List<String>? = null,
    val imageDefault: String? = null,
    val imageShiny: String? = null
)