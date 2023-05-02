package com.echo.pokepedia.domain.pokemon.model.network

import com.google.gson.annotations.SerializedName

data class Other(
    @SerializedName("official-artwork") val official_artwork: OfficialArtwork
)
