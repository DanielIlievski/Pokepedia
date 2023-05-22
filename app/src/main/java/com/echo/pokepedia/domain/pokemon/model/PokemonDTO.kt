package com.echo.pokepedia.domain.pokemon.model

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
        return PokemonEntity(id!!, name!!, url!!, urlShiny!!, dominantColor!!, dominantColorShiny!!)
    }
}