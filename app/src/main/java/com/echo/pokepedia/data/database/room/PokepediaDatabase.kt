package com.echo.pokepedia.data.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.echo.pokepedia.domain.pokemon.model.database.PokemonDetailsEntity
import com.echo.pokepedia.domain.pokemon.model.database.PokemonEntity
import com.echo.pokepedia.domain.pokemon.model.database.StatEntity
import com.echo.pokepedia.domain.pokemon.model.database.typeconverter.StringListConverter

@Database(
    entities = [
        PokemonEntity::class,
        PokemonDetailsEntity::class,
        StatEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class PokepediaDatabase : RoomDatabase() {

//    companion object {
//        @Volatile
//        private var INSTANCE: PokepediaDatabase? = null
//
//        fun getDatabase(context: Context): PokepediaDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context,
//                    PokepediaDatabase::class.java,
//                    "pokepedia_db"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }

    abstract fun pokemonDao(): PokemonDao
    abstract fun statDao(): StatDao
}