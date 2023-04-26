package com.echo.pokepedia.ui.pokemon.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.ListItemPokemonBinding
import com.echo.pokepedia.domain.pokemon.model.PokemonDTO
import com.echo.pokepedia.util.capitalizeFirstLetter
import com.echo.pokepedia.util.loadImageCalcDominantColor

class PokemonAdapter(
    private val onItemClicked: (PokemonDTO) -> Unit
) : ListAdapter<PokemonDTO, PokemonAdapter.PokemonViewHolder>(DiffCallback) {

    inner class PokemonViewHolder(private val binding: ListItemPokemonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pokemon: PokemonDTO) {
            with(binding) {
                textPokemonName.text = pokemon.name?.capitalizeFirstLetter()
                imgPokemon.loadImageCalcDominantColor(
                    root.context,
                    pokemon.url
                ) { gradientDrawable ->
                    gradientDrawable.cornerRadius =
                        binding.root.context.resources.getDimension(R.dimen.radius_medium)
                    cardPokemon.setCardBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT))
                    cardPokemon.background = gradientDrawable
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PokemonAdapter.PokemonViewHolder {
        val binding =
            ListItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val currentPokemon = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(currentPokemon)
        }
        holder.bind(currentPokemon)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<PokemonDTO>() {
            override fun areItemsTheSame(oldItem: PokemonDTO, newItem: PokemonDTO): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: PokemonDTO, newItem: PokemonDTO): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }

}