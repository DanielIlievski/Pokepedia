package com.echo.pokepedia.util

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat


fun Context.getColorRes(@ColorRes res: Int): Int {
    return ContextCompat.getColor(this, res)
}