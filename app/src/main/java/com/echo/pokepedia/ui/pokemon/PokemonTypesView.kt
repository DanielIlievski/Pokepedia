package com.echo.pokepedia.ui.pokemon

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import com.echo.pokepedia.databinding.ViewPokemonTypesBinding
import com.echo.pokepedia.util.capitalizeFirstLetter
import com.echo.pokepedia.util.getColorRes
import com.echo.pokepedia.util.parseTypeToColorRes
import com.echo.pokepedia.util.parseTypeToDrawableRes
import com.google.android.material.button.MaterialButton

class PokemonTypesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var binding: ViewPokemonTypesBinding

    init {
        binding = ViewPokemonTypesBinding.inflate(LayoutInflater.from(context), this)
    }

    fun render(types: List<String>, orientation: Int, visibility: Int) {
        setOrientation(orientation)
        if (types.size == 2) {
            initProperties(binding.type1, types[0], visibility)
            initProperties(binding.type2, types[1], visibility)
        } else {
            setOrientation(orientation)
            initProperties(binding.type1, types[0], visibility)
            binding.type2.visibility = View.GONE
        }
    }

    private fun initProperties(button: MaterialButton, type: String, visibility: Int) {
        with(button) {
            text = type.capitalizeFirstLetter()
            icon = AppCompatResources.getDrawable(
                binding.root.context,
                parseTypeToDrawableRes(type)
            )
            setBackgroundColor(rootView.context.getColorRes(parseTypeToColorRes(type)))
            this.visibility = visibility
        }
    }
}