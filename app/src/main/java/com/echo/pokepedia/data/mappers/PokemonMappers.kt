package com.echo.pokepedia.data.mappers

import com.echo.pokepedia.domain.pokemon.model.PokemonDTO
import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO
import com.echo.pokepedia.domain.pokemon.model.database.PokemonEntity
import com.echo.pokepedia.domain.pokemon.model.database.relation.PokemonDetailsWithStats
import com.echo.pokepedia.domain.pokemon.model.network.PokemonDetailsResponse
import com.echo.pokepedia.domain.pokemon.model.network.PokemonResponse

fun PokemonResponse.toPokemonDTO(): PokemonDTO {
    val id = if(url.endsWith("/")) {
        url.dropLast(1).takeLastWhile { it.isDigit() }
    } else {
        url.takeLastWhile { it.isDigit() }
    }
    val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${id}.png"
    val imageUrlShiny = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/${id}.png"
    return PokemonDTO(id.toInt(), name, imageUrl, imageUrlShiny)
}

fun PokemonDetailsResponse.toPokemonDetailsDTO(): PokemonDetailsDTO {
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

fun PokemonEntity.toPokemonDTO(): PokemonDTO {
    return PokemonDTO(
        id,
        name,
        url,
        urlShiny,
        dominantColor,
        dominantColorShiny)
}

fun PokemonDetailsWithStats.toPokemonDetailsDTO(): PokemonDetailsDTO {
    val statsTriple = statsList.map { stat ->
        Triple(stat.name, stat.baseStat, stat.maxBaseStat)
    }
    with(pokemonDetails) {
        return PokemonDetailsDTO(
            id,
            name,
            types,
            abilities,
            statsTriple,
            imageDefault,
            imageShiny
        )
    }
}