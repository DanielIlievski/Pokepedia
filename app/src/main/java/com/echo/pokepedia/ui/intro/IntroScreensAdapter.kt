package com.echo.pokepedia.ui.intro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.echo.pokepedia.data.model.IntroScreen
import com.echo.pokepedia.data.model.getIntroScreens
import com.echo.pokepedia.databinding.ItemViewPagerBinding

class IntroScreensAdapter() : RecyclerView.Adapter<IntroScreensAdapter.ViewPagerViewHolder>() {

    private val introScreens = getIntroScreens()

    class ViewPagerViewHolder(private val binding: ItemViewPagerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(screen: IntroScreen) {
            with(binding) {
                titleViewPager.text = screen.title
                descriptionViewPager.text = screen.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val binding =
            ItemViewPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewPagerViewHolder(binding)
    }

    override fun getItemCount() = introScreens.size

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val screen = introScreens[position]
        holder.bind(screen)
    }
}