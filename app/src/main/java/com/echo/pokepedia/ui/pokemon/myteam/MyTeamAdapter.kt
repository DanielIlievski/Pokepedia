package com.echo.pokepedia.ui.pokemon.myteam

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.ListItemTeamMemberBinding
import com.echo.pokepedia.domain.pokemon.model.PokemonDTO
import com.echo.pokepedia.util.capitalizeFirstLetter
import com.echo.pokepedia.util.getColorRes

class MyTeamAdapter(
    private val myTeamList: MutableList<PokemonDTO>,
    private val isTeamFull: Boolean,
    private val height: Int,
    private val onDeleteClick: (PokemonDTO) -> Unit
) : RecyclerView.Adapter<MyTeamAdapter.MyTeamViewHolder>() {

    inner class MyTeamViewHolder(private val binding: ListItemTeamMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(currentItem: PokemonDTO) {
            initFrontView(binding, currentItem)
            initBack(binding, currentItem)
        }

        fun bindDefault() {
            with(binding.listItemFront) {
                binding.root.isFlipEnabled = false
                imgPokemon.animation = null
                btnDelete.visibility = View.GONE
                imgPokemon.setImageResource(R.drawable.ic_add_pokemon)
                imgPokemon.backgroundTintList =
                    ColorStateList.valueOf(root.context.getColorRes(R.color.grey_light))
            }
        }
    }

    private fun initFrontView(
        binding: ListItemTeamMemberBinding,
        currentItem: PokemonDTO
    ) {
        with(binding.listItemFront) {
            imgPokemon.background = currentItem.dominantColor?.let {
                getDrawableWhiteBottomRounded(it)
            }
            Glide.with(root)
                .load(currentItem.url)
                .placeholder(R.drawable.progress_spinner_anim)
                .into(imgPokemon)
            imgPokemon.animation =
                if (isTeamFull)
                    android.view.animation.AnimationUtils.loadAnimation(root.context, R.anim.shake)
                else null
            root.setOnClickListener {
                binding.root.flipTheView()
            }
            btnDelete.visibility = View.VISIBLE
            btnDelete.setOnClickListener {
                onDeleteClick(currentItem)
            }
        }
    }

    private fun initBack(
        binding: ListItemTeamMemberBinding,
        currentItem: PokemonDTO
    ) {
        with(binding.listItemBack) {
            pokemonId.text =
                root.context.getString(R.string.pokemon_id, currentItem.id)
            pokemonName.text =
                root.context.getString(
                    R.string.pokemon_name,
                    currentItem.name?.capitalizeFirstLetter()
                )
            root.background = currentItem.dominantColor?.let {
                getDrawableWhiteBottomRounded(it)
            }
            root.setOnClickListener {
                binding.root.flipTheView()
            }
        }
    }

    private fun getDrawableWhiteBottomRounded(color: Int): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                color,
                Color.WHITE
            )
        ).apply { cornerRadii = floatArrayOf(60f, 60f, 60f, 60f, 60f, 60f, 60f, 60f) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyTeamViewHolder {
        val binding =
            ListItemTeamMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyTeamViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 6
    }

    override fun onBindViewHolder(holder: MyTeamViewHolder, position: Int) {
        holder.itemView.rootView.layoutParams.height = height / 3
        if (position < myTeamList.size) {
            val currentItem = myTeamList[position]
            holder.bind(currentItem)
        } else {
            holder.bindDefault()
        }
    }
}