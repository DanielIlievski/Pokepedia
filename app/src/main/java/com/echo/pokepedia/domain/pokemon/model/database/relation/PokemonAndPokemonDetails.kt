package com.echo.pokepedia.domain.pokemon.model.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.echo.pokepedia.domain.pokemon.model.database.PokemonDetailsEntity
import com.echo.pokepedia.domain.pokemon.model.database.PokemonEntity

data class PokemonAndPokemonDetails(
    @Embedded val pokemon: PokemonEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val pokemonDetails: PokemonDetailsEntity
)