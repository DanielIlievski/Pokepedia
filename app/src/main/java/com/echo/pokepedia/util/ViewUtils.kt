package com.echo.pokepedia.util

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.echo.pokepedia.R

fun ImageView.loadImageFromUrlAndCalculateDominantColorGradient(
    context: Context,
    url: String?,
    onFinish: (Int) -> Unit
) {
    Glide.with(context)
        .load(url)
        .placeholder(R.drawable.progress_spinner_anim)
        .error(AppCompatResources.getDrawable(context, R.drawable.image_not_available))
        .into(object : CustomTarget<Drawable>() {
            override fun onLoadStarted(placeholder: Drawable?) {
                this@loadImageFromUrlAndCalculateDominantColorGradient.setImageDrawable(placeholder)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                this@loadImageFromUrlAndCalculateDominantColorGradient.setImageDrawable(
                    errorDrawable
                )
                val dominantColor = calcDominantColor(errorDrawable, context)
                onFinish(dominantColor)
            }

            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                val dominantColor = calcDominantColor(resource, context)
                onFinish(dominantColor)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
}

fun calcDominantColor(resource: Drawable?, context: Context): Int {
    val palette = Palette.from((resource as BitmapDrawable).bitmap).generate()
    return palette.getDominantColor(ContextCompat.getColor(context, R.color.white))
}