package com.echo.pokepedia.domain.pokemon.model.network

import com.echo.pokepedia.domain.pokemon.Pokemon

data class PokemonDTO(
    val id: Int,
    val name: String,
    val types: List<Type>,
    val abilities: List<Ability>,
    val moves: List<Move>,
    val sprites: Sprites
) {
    fun toPokemon(): Pokemon {
        val myTypes: List<String> = types.map { it.type.name }
        val myAbilities: List<String> = abilities.map { it.ability.name }
        val myMoves: List<String> = moves.map { it.move.name }
        val imageDefault: String = sprites.other.home.front_default
        val imageShiny: String = sprites.other.home.front_shiny
        return Pokemon(
            id,
            name,
            myTypes,
            myAbilities,
            myMoves,
            imageDefault,
            imageShiny
        )
    }
}