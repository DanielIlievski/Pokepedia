package com.echo.pokepedia.domain.pokemon.model.network

data class PokemonListResponse(
    val count: Int,
    val results: List<PokemonResponse>
)