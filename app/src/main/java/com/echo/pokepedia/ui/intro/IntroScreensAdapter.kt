package com.echo.pokepedia.ui.intro

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.echo.pokepedia.databinding.ItemViewPagerBinding
import com.echo.pokepedia.domain.authentication.model.IntroScreen
import com.echo.pokepedia.domain.authentication.model.getIntroScreens

class IntroScreensAdapter() : RecyclerView.Adapter<IntroScreensAdapter.ViewPagerViewHolder>() {

    private val introScreens = getIntroScreens()

    class ViewPagerViewHolder(private val binding: ItemViewPagerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val context: Context = binding.root.context
        fun bind(screen: IntroScreen) {
            with(binding) {
                titleViewPager.text = context.getString(screen.title)
                descriptionViewPager.text = context.getString(screen.description)

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