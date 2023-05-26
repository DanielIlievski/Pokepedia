package com.echo.pokepedia.domain.pokemon.model.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.echo.pokepedia.domain.pokemon.model.database.PokemonDetailsEntity
import com.echo.pokepedia.domain.pokemon.model.database.StatEntity

data class PokemonDetailsWithStats(
    @Embedded val pokemonDetails: PokemonDetailsEntity,
    @Relation(parentColumn = "id", entityColumn = "pokemon_id")
    val statsList: List<StatEntity>
)