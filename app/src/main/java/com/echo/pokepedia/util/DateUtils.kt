package com.echo.pokepedia.util

import java.text.SimpleDateFormat
import java.util.*

fun Date.toDDMMMYYYY(): String {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return dateFormatter.format(this)
}