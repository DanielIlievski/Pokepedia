package com.echo.pokepedia.data.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.echo.pokepedia.data.database.room.authentication.UserDao
import com.echo.pokepedia.data.database.room.pokemon.PokemonDao
import com.echo.pokepedia.data.database.room.pokemon.StatDao
import com.echo.pokepedia.data.database.room.pokemon.TeamMemberDao
import com.echo.pokepedia.domain.authentication.model.database.UserEntity
import com.echo.pokepedia.domain.authentication.model.database.typeconverter.DateTypeConverter
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
        TeamMemberEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class, DateTypeConverter::class)
abstract class PokepediaDatabase : RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao
    abstract fun statDao(): StatDao
    abstract fun teamMemberDao(): TeamMemberDao
    abstract fun userDao(): UserDao
}