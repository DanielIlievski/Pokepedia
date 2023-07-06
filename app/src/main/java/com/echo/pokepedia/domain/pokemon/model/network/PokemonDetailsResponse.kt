package com.echo.pokepedia.domain.pokemon.model.network

data class PokemonDetailsResponse(
    val id: Int,
    val name: String,
    val types: List<Type>,
    val abilities: List<Ability>,
    val stats: List<Stat>,
    val sprites: Sprites
)