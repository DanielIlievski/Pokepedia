package com.echo.pokepedia.util

fun String.capitalizeFirstLetter(): String {
    return this.replaceFirstChar { it.uppercase() }
}

fun getUsername(firstName: String, lastName: String): String {
    return "$firstName $lastName"
}