package com.echo.pokepedia.util

fun String.capitalizeFirstLetter(): String {
    return this.replaceFirstChar { it.uppercase() }
}