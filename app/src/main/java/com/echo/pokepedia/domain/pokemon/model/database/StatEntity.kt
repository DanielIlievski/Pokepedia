package com.echo.pokepedia.domain.pokemon.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("stat")
data class StatEntity(
    val name: String,
    @ColumnInfo("base_stat")
    val baseStat: Int,
    @ColumnInfo("max_base_stat")
    val maxBaseStat: Int,
    @ColumnInfo("pokemon_id")
    val pokemonId: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)