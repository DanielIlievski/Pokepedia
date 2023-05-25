package com.echo.pokepedia.data.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.echo.pokepedia.domain.pokemon.model.database.PokemonDetailsEntity
import com.echo.pokepedia.domain.pokemon.model.database.PokemonEntity
import com.echo.pokepedia.domain.pokemon.model.database.StatEntity
import com.echo.pokepedia.domain.pokemon.model.database.TeamMemberEntity
import com.echo.pokepedia.domain.pokemon.model.database.typeconverter.StringListConverter

@Database(
    entities = [
        PokemonEntity::class,
        PokemonDetailsEntity::class,
        StatEntity::class,
        TeamMemberEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class PokepediaDatabase : RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao
    abstract fun statDao(): StatDao
    abstract fun teamMemberDao(): TeamMemberDao
}