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

class PokemonTypesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var binding: ViewPokemonTypesBinding

    init {
        binding = ViewPokemonTypesBinding.inflate(LayoutInflater.from(context), this)
    }

    fun render(types: List<String>, orientation: Int) {
        setOrientation(orientation)
        if (types.size == 2) {
            with(binding.type1) {
                text = types[0].capitalizeFirstLetter()
                icon = AppCompatResources.getDrawable(
                    binding.root.context,
                    parseTypeToDrawableRes(types[0])
                )
                setBackgroundColor(rootView.context.getColorRes(parseTypeToColorRes(types[0])))
            }

            with(binding.type2) {
                text = types[1].capitalizeFirstLetter()
                icon = AppCompatResources.getDrawable(
                    binding.root.context,
                    parseTypeToDrawableRes(types[1])
                )
                setBackgroundColor(rootView.context.getColorRes(parseTypeToColorRes(types[1])))
            }
        } else {
            setOrientation(orientation)
            with(binding.type1) {
                text = types[0].capitalizeFirstLetter()
                icon = AppCompatResources.getDrawable(
                    binding.root.context,
                    parseTypeToDrawableRes(types[0])
                )
                setBackgroundColor(rootView.context.getColorRes(parseTypeToColorRes(types[0])))
            }

            binding.type2.visibility = View.GONE
        }
    }
}