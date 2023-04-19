package com.echo.pokepedia.util

fun String.capitalizeFirstLetter(): String {
    return if (isNotEmpty()) {
        val firstChar = this[0]
        if (Character.isLowerCase(firstChar)) {
            Character.toUpperCase(firstChar) + substring(1)
        } else {
            this
        }
    } else {
        this
    }
}