package com.echo.pokepedia.data.database.room.pokemon

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.echo.pokepedia.domain.pokemon.model.database.TeamMemberEntity
import com.echo.pokepedia.domain.pokemon.model.database.relation.PokemonAndTeamMember
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamMemberDao {

    @Upsert
    suspend fun upsertTeamMember(teamMember: TeamMemberEntity)

    @Transaction
    @Query("SELECT * FROM pokemon")
    fun getAllTeamMembers(): Flow<List<PokemonAndTeamMember>>

    @Query("DELETE FROM team_member WHERE pokemon_id IS :pokemonId")
    suspend fun deleteTeamMember(pokemonId: Int)
}