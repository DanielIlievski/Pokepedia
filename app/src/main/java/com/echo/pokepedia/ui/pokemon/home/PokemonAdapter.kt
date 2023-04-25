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
import com.echo.pokepedia.OnBottomReachedListener
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.ListItemPokemonBinding
import com.echo.pokepedia.domain.pokemon.model.Pokemon
import com.echo.pokepedia.util.getColorRes
import com.echo.pokepedia.util.loadImageFromUrlAndCalculateDominantColorGradient

class PokemonAdapter(
    private val onItemClicked: (Pokemon) -> Unit
) : PagingDataAdapter<Pokemon, PokemonAdapter.PokemonViewHolder>(DiffCallback) {

    private lateinit var onBottomReachedListener: OnBottomReachedListener

    fun setOnBottomReachedListener(onBottomReachedListener: OnBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener
    }

    inner class PokemonViewHolder(private val binding: ListItemPokemonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pokemon: Pokemon) {
            binding.textPokemonName.text = pokemon.name?.replaceFirstChar { it.uppercase() }
            // calculate the dominant color the first time the item is shown and update the model
            if (pokemon.dominantColor == null) {
                Glide.with(binding.root.context)
                    .load(pokemon.url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.progress_spinner_anim)
                    .error(
                        AppCompatResources.getDrawable(
                            binding.root.context,
                            R.drawable.image_not_available
                        )
                    )
                    .into(binding.imgPokemon)
                binding.imgPokemon.loadImageFromUrlAndCalculateDominantColorGradient(
                    binding.root.context,
                    pokemon.url
                ) { dominantColor ->
                    val gradientDrawable = GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        intArrayOf(dominantColor, binding.root.context.getColorRes(R.color.white))
                    )
                    pokemon.dominantColor = dominantColor
                    gradientDrawable.cornerRadius =
                        binding.root.context.resources.getDimension(R.dimen.radius_medium)
                    binding.cardPokemon.background = gradientDrawable
                }
            }
            // read the dominant color from the item
            else {
                Log.d("HelloWorld", "bind: dominant color ${pokemon.dominantColor}")
                Glide.with(binding.root)
                    .load(pokemon.url)
                    .placeholder(R.drawable.progress_spinner_anim)
                    .error(
                        AppCompatResources.getDrawable(
                            binding.root.context,
                            R.drawable.image_not_available
                        )
                    )
                    .into(binding.imgPokemon)
                val gradientDrawable = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(
                        pokemon.dominantColor!!,
                        binding.root.context.getColorRes(R.color.white)
                    )
                )
                gradientDrawable.cornerRadius =
                    binding.root.context.resources.getDimension(R.dimen.radius_medium)
                binding.cardPokemon.background = gradientDrawable
            }
            binding.cardPokemon.setOnClickListener {
                onItemClicked(pokemon)
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

//        if (position == itemCount - 7) {
//            onBottomReachedListener.onBottomReached(position)
//        }
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