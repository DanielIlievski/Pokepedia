package com.echo.pokepedia.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.echo.pokepedia.R

open class BaseFragment : Fragment() {

    fun showToastMessageShort(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun showToastMessageLong(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    fun showToastMessageCustom(message: String?, duration: Int) {
        Toast.makeText(requireContext(), message, duration).show()
    }

    fun getGradientWhiteBottom(color: Int): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                color,
                Color.WHITE
            )
        )
    }

    fun getGradientBlackTop(color: Int): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(
                color,
                Color.BLACK
            )
        )
    }

    fun loadImage(imgUrl: String?, imageView: ImageView) {
        Glide.with(requireContext())
            .load(imgUrl)
            .placeholder(R.drawable.progress_spinner_anim)
            .into(imageView)
    }
}