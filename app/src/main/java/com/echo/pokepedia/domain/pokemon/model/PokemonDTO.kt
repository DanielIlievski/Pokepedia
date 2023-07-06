package com.echo.pokepedia.domain.pokemon.model

import android.graphics.Color
import com.echo.pokepedia.domain.pokemon.model.database.PokemonEntity

data class PokemonDTO(
    val id: Int? = null,
    val name: String? = null,
    val url: String? = null,
    val urlShiny: String? = null,
    var dominantColor: Int? = null,
    var dominantColorShiny: Int? = null
) {
    fun overrideColors(dominantColor: Int, dominantColorShiny: Int): PokemonDTO {
        return this.copy(
            dominantColor = dominantColor,
            dominantColorShiny = dominantColorShiny
        )
    }

    fun toPokemonEntity(): PokemonEntity {
        return PokemonEntity(
            id = id ?: -1,
            name = name.orEmpty(),
            url = url.orEmpty(),
            urlShiny = urlShiny.orEmpty(),
            dominantColor = dominantColor ?: Color.WHITE,
            dominantColorShiny = dominantColorShiny ?: Color.WHITE)
    }
}