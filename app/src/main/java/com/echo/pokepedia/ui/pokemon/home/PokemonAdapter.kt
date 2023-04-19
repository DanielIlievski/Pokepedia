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
import com.echo.pokepedia.domain.pokemon.model.Pokemon
import com.echo.pokepedia.util.capitalizeFirstLetter
import com.echo.pokepedia.util.loadImageFromUrlAndCalculateDominantColorGradient

class PokemonAdapter(
    private val onItemClicked: (Pokemon) -> Unit
) : ListAdapter<Pokemon, PokemonAdapter.PokemonViewHolder>(DiffCallback) {

    inner class PokemonViewHolder(private val binding: ListItemPokemonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pokemon: Pokemon) {
            binding.textPokemonName.text = pokemon.name?.capitalizeFirstLetter()
            binding.imgPokemon.loadImageFromUrlAndCalculateDominantColorGradient(binding.root.context, pokemon.url) { gradientDrawable ->
                gradientDrawable.cornerRadius = binding.root.context.resources.getDimension(R.dimen.radius_medium)
                binding.cardPokemon.setCardBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT))
                binding.cardPokemon.background = gradientDrawable
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
        private val DiffCallback = object : DiffUtil.ItemCallback<Pokemon>() {
            override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }

}