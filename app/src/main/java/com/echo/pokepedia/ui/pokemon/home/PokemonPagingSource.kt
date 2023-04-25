package com.echo.pokepedia.ui.pokemon.home

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.echo.pokepedia.data.network.RemotePokemonDataSource
import com.echo.pokepedia.domain.pokemon.model.Pokemon
import com.echo.pokepedia.util.NetworkResult
import com.echo.pokepedia.util.PAGE_SIZE
import com.echo.pokepedia.util.STARTING_PAGE_INDEX

class PokemonPagingSource(
    private val remotePokemonDataSource: RemotePokemonDataSource
) : PagingSource<Int, Pokemon>() {
    override fun getRefreshKey(state: PagingState<Int, Pokemon>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pokemon> {
        try {
            val nextPageNumber = params.key ?: STARTING_PAGE_INDEX
            val response = remotePokemonDataSource.getPokemonList(PAGE_SIZE, nextPageNumber)
            lateinit var data : List<Pokemon>
            when (response) {
                is NetworkResult.Success -> data = response.result!!.results.map { it.toPokemon() }
                is NetworkResult.Failure -> return LoadResult.Error(Exception(response.exception.toString()))
            }
            val prevKey = if (nextPageNumber == STARTING_PAGE_INDEX) null else nextPageNumber - PAGE_SIZE
            val nextKey = if (data.isEmpty()) null else nextPageNumber + PAGE_SIZE
            return LoadResult.Page(data, prevKey, nextKey)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

}