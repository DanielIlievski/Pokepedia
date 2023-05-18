package com.echo.pokepedia.domain.pokemon.model.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.echo.pokepedia.domain.pokemon.model.PokemonDetailsDTO
import com.echo.pokepedia.domain.pokemon.model.database.PokemonDetailsEntity
import com.echo.pokepedia.domain.pokemon.model.database.StatEntity

data class PokemonDetailsWithStats(
    @Embedded val pokemonDetails: PokemonDetailsEntity,
    @Relation(parentColumn = "id", entityColumn = "pokemon_id")
    val statsList: List<StatEntity>
) {
    fun toPokemonDetailsDTO(): PokemonDetailsDTO {
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
}