package com.echo.pokepedia.domain.pokemon.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "team_member")
data class TeamMemberEntity(
    @ColumnInfo("pokemon_id") val pokemonId: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)