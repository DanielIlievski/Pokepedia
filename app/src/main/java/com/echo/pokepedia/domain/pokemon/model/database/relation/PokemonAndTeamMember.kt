package com.echo.pokepedia.domain.pokemon.model.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.echo.pokepedia.domain.pokemon.model.database.PokemonEntity
import com.echo.pokepedia.domain.pokemon.model.database.TeamMemberEntity

data class PokemonAndTeamMember (
    @Embedded val pokemon: PokemonEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "pokemon_id"
    )
    val teamMember: TeamMemberEntity?
)
