package com.echo.pokepedia.domain.pokemon.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.echo.pokepedia.domain.pokemon.model.PokemonDTO

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("url") val url: String,
    @ColumnInfo("url_shiny") val urlShiny: String,
    @ColumnInfo("dominant_color") var dominantColor: Int,
    @ColumnInfo("dominant_color_shiny") var dominantColorShiny: Int
) {
    fun toPokemonDTO(): PokemonDTO {
        return PokemonDTO(
            id,
            name,
            url,
            urlShiny,
            dominantColor,
            dominantColorShiny)
    }
}