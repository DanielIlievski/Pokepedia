package com.echo.pokepedia.domain.pokemon.model.network

import com.echo.pokepedia.domain.pokemon.model.Pokemon

data class PokemonDTO(
    val name: String,
    val url: String
) {
    fun toPokemon(): Pokemon {
        val number = if(url.endsWith("/")) {
            url.dropLast(1).takeLastWhile { it.isDigit() }
        } else {
            url.takeLastWhile { it.isDigit() }
        }
        val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/${number}.png"
        return Pokemon(name, imageUrl)
    }
}