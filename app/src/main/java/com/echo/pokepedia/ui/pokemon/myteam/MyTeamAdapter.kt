package com.echo.pokepedia.ui.pokemon.myteam

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.echo.pokepedia.R
import com.echo.pokepedia.databinding.ListItemTeamMemberBinding
import com.echo.pokepedia.domain.pokemon.model.PokemonDTO
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
            with(binding) {
                imgPokemon.background = currentItem.dominantColor?.let {
                    getDrawableWhiteBottom(it)
                }
                Glide.with(root)
                    .load(currentItem.url)
                    .placeholder(R.drawable.progress_spinner_anim)
                    .into(imgPokemon)
                imgPokemon.animation =
                    if (isTeamFull) AnimationUtils.loadAnimation(root.context, R.anim.shake)
                    else null
                btnDelete.visibility = View.VISIBLE
                btnDelete.setOnClickListener {
                    onDeleteClick(currentItem)
                }
            }
        }

        fun bindDefault() {
            with(binding) {
                imgPokemon.animation = null
                btnDelete.visibility = View.GONE
                imgPokemon.setImageResource(R.drawable.ic_add_pokemon)
                imgPokemon.backgroundTintList =
                    ColorStateList.valueOf(root.context.getColorRes(R.color.grey_light))
            }
        }

        private fun getDrawableWhiteBottom(color: Int): GradientDrawable {
            return GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    color,
                    Color.WHITE
                )
            )
        }
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