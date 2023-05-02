package com.echo.pokepedia.domain.pokemon.model.network

import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO

data class PokemonDetailsResponse(
    val id: Int,
    val name: String,
    val types: List<Type>,
    val abilities: List<Ability>,
    val stats: List<Stat>,
    val sprites: Sprites
) {
    fun toPokemonDetailsDTO(): PokemonDetailsDTO {
        val myTypes: List<String> = types.map { it.type.name }
        val myAbilities: List<String> = abilities.map { it.ability.name }
        var maxBaseStat = 0
        stats.forEach { if(it.base_stat > maxBaseStat) maxBaseStat = it.base_stat }
        val myStats: List<Triple<String, Int, Int>> = stats.map { Triple(it.stat.name, it.base_stat, maxBaseStat) }
        val imageDefault: String? = sprites.other.official_artwork.front_default
        val imageShiny: String? = sprites.other.official_artwork.front_shiny
        return PokemonDetailsDTO(
            id,
            name,
            myTypes,
            myAbilities,
            myStats,
            imageDefault,
            imageShiny
        )
    }
}