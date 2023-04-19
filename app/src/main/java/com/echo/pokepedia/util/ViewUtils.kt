package com.echo.pokepedia.util

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.echo.pokepedia.R

fun ImageView.loadImageFromUrlAndCalculateDominantColorGradient(context: Context, url: String?, onFinish: (GradientDrawable) -> Unit) {
    Glide.with(context)
        .load(url)
        .placeholder(R.drawable.progress_spinner_anim)
        .into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                this@loadImageFromUrlAndCalculateDominantColorGradient.setImageDrawable(resource)
                val dominantColor = calcDominantColor(resource, context)
                val gradientDrawable = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(dominantColor, context.getColorRes(R.color.white))
                )
                onFinish(gradientDrawable)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
}

private fun calcDominantColor(resource: Drawable, context: Context) : Int {
    val palette = Palette.from((resource as BitmapDrawable).bitmap).generate()
    val dominantColor = palette.getDominantColor(ContextCompat.getColor(context, R.color.white))

    return dominantColor
}