package com.echo.pokepedia.ui.pokemon.home

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.ListItemPokemonBinding
import com.echo.pokepedia.domain.pokemon.model.PokemonDTO
import com.echo.pokepedia.util.getColorRes
import com.echo.pokepedia.util.loadImageCalcDominantColor

class PokemonAdapter(
    private val onItemClicked: (PokemonDTO) -> Unit
) : PagingDataAdapter<PokemonDTO, PokemonAdapter.PokemonViewHolder>(DiffCallback) {

    inner class PokemonViewHolder(private val binding: ListItemPokemonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pokemon: PokemonDTO) {
            with(binding) {
                textPokemonName.text = pokemon.name?.replaceFirstChar { it.uppercase() }
                // calculate the dominant color the first time the item is shown and update the model
                if (pokemon.dominantColor == null) {
                    Glide.with(root.context)
                        .load(pokemon.url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.progress_spinner_anim)
                        .error(
                            AppCompatResources.getDrawable(
                                root.context,
                                R.drawable.image_not_available
                            )
                        )
                        .into(imgPokemon)
                    imgPokemon.loadImageCalcDominantColor(
                        root.context,
                        pokemon.url
                    ) { dominantColor ->
                        val gradientDrawable = GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            intArrayOf(dominantColor, root.context.getColorRes(R.color.white))
                        )
                        pokemon.dominantColor = dominantColor
                        gradientDrawable.cornerRadius =
                            root.context.resources.getDimension(R.dimen.radius_medium)
                        cardPokemon.background = gradientDrawable
                    }
                }
                // read the dominant color from the item
                else {
                    Log.d("HelloWorld", "bind: dominant color ${pokemon.dominantColor}")
                    Glide.with(root)
                        .load(pokemon.url)
                        .placeholder(R.drawable.progress_spinner_anim)
                        .error(
                            AppCompatResources.getDrawable(
                                root.context,
                                R.drawable.image_not_available
                            )
                        )
                        .into(imgPokemon)
                    val gradientDrawable = GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        intArrayOf(
                            pokemon.dominantColor!!,
                            root.context.getColorRes(R.color.white)
                        )
                    )
                    gradientDrawable.cornerRadius =
                        root.context.resources.getDimension(R.dimen.radius_medium)
                    cardPokemon.background = gradientDrawable
                }
                cardPokemon.setOnClickListener {
                    onItemClicked(pokemon)
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
        currentPokemon?.let { holder.bind(it) }
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