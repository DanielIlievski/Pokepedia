package com.echo.pokepedia.domain.pokemon.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_details")
data class PokemonDetailsEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("types") val types: List<String>,
    @ColumnInfo("abilities") val abilities: List<String>,
    @ColumnInfo("image_default") val imageDefault: String,
    @ColumnInfo("image_shiny") val imageShiny: String
)