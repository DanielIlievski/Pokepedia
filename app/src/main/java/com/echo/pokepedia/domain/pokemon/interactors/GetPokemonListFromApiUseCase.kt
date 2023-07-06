package com.echo.pokepedia.domain.pokemon.interactors

import androidx.paging.PagingData
import com.echo.pokepedia.domain.pokemon.model.PokemonDTO
import com.echo.pokepedia.domain.pokemon.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPokemonListFromApiUseCase @Inject constructor(
    private val pokemonRepository: PokemonRepository
) {

    suspend operator fun invoke(): Flow<PagingData<PokemonDTO>> {
        return pokemonRepository.getPokemonList()
    }
}