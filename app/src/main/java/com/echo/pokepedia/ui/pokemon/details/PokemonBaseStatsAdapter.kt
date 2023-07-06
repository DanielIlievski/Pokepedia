package com.echo.pokepedia.ui.pokemon.details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.echo.pokepedia.databinding.ListItemBaseStatBinding
import com.echo.pokepedia.util.getColorRes
import com.echo.pokepedia.util.parseStatNameAbbr
import com.echo.pokepedia.util.parseStatToColor
import kotlin.math.roundToInt

class PokemonBaseStatsAdapter(
    private val statsList: List<Triple<String, Int, Int>>
) :
    RecyclerView.Adapter<PokemonBaseStatsAdapter.PokemonBaseStatsViewHolder>() {

    inner class PokemonBaseStatsViewHolder(private val binding: ListItemBaseStatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("NewApi")
        fun bind(stat: Triple<String, Int, Int>) {
            binding.indicatorBaseStat.apply {
                val progressValue = (stat.second * 1.0) / stat.third * 100
                setProgress(progressValue.roundToInt(), true)
                setIndicatorColor(binding.root.context.getColorRes(parseStatToColor(stat.first)))
            }
            binding.statName.text = parseStatNameAbbr(stat.first)
            binding.baseStat.text = stat.second.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonBaseStatsViewHolder {
        val binding =
            ListItemBaseStatBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PokemonBaseStatsViewHolder(binding)
    }

    override fun getItemCount() = statsList.size

    override fun onBindViewHolder(holder: PokemonBaseStatsViewHolder, position: Int) {
        val currentStat = statsList[position]
        holder.bind(currentStat)
    }
}