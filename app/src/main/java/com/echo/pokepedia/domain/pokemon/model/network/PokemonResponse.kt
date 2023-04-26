package com.echo.pokepedia.domain.pokemon.model.network

import com.echo.pokepedia.domain.pokemon.model.PokemonDTO

data class PokemonResponse(
    val name: String,
    val url: String
) {
    fun toPokemonDTO(): PokemonDTO {
        val number = if(url.endsWith("/")) {
            url.dropLast(1).takeLastWhile { it.isDigit() }
        } else {
            url.takeLastWhile { it.isDigit() }
        }
        val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${number}.png"
        return PokemonDTO(name, imageUrl)
    }
}