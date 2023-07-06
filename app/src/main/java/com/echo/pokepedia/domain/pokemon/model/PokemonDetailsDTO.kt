package com.echo.pokepedia.domain.pokemon.model

import com.echo.pokepedia.domain.pokemon.model.database.PokemonDetailsEntity
import com.echo.pokepedia.domain.pokemon.model.database.StatEntity

data class PokemonDetailsDTO(
    val id: Int? = null,
    val name: String? = null,
    val types: List<String>? = null,
    val abilities: List<String>? = null,
    val stats: List<Triple<String, Int, Int>>? = null,
    val imageDefault: String? = null,
    val imageShiny: String? = null
) {
    fun toPokemonDetailsEntity(): PokemonDetailsEntity {
        return PokemonDetailsEntity(
            id = id ?: -1,
            name = name.orEmpty(),
            types = types ?: emptyList(),
            abilities = abilities ?: emptyList(),
            imageDefault = imageDefault.orEmpty(),
            imageShiny = imageShiny.orEmpty()
        )
    }

    fun toStatEntityList(): List<StatEntity> {
        return stats?.map { stat ->
            StatEntity(
                name = stat.first,
                baseStat = stat.second,
                maxBaseStat = stat.third,
                pokemonId = id ?: -1
            )
        } ?: emptyList()
    }
}