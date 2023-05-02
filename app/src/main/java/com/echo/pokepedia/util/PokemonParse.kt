package com.echo.pokepedia.util

import com.echo.pokepedia.R

fun parseTypeToColorRes(type: String): Int {
    return when (type) {
        "normal" -> R.color.type_normal
        "fire" -> R.color.type_fire
        "water" -> R.color.type_water
        "electric" -> R.color.type_electric
        "grass" -> R.color.type_grass
        "ice" -> R.color.type_ice
        "fighting" -> R.color.type_fighting
        "poison" -> R.color.type_poison
        "ground" -> R.color.type_ground
        "flying" -> R.color.type_flying
        "psychic" -> R.color.type_psychic
        "bug" -> R.color.type_bug
        "rock" -> R.color.type_rock
        "ghost" -> R.color.type_ghost
        "dragon" -> R.color.type_dragon
        "dark" -> R.color.type_dark
        "steel" -> R.color.type_steel
        "fairy" -> R.color.type_fairy
        else -> R.color.black
    }
}

fun parseTypeToDrawableRes(type: String): Int {
    return when (type) {
        "normal" -> R.drawable.type_normal
        "fire" -> R.drawable.type_fire
        "water" -> R.drawable.type_water
        "electric" -> R.drawable.type_electric
        "grass" -> R.drawable.type_grass
        "ice" -> R.drawable.type_ice
        "fighting" -> R.drawable.type_fighting
        "poison" -> R.drawable.type_poison
        "ground" -> R.drawable.type_ground
        "flying" -> R.drawable.type_flying
        "psychic" -> R.drawable.type_psychic
        "bug" -> R.drawable.type_bug
        "rock" -> R.drawable.type_rock
        "ghost" -> R.drawable.type_ghost
        "dragon" -> R.drawable.type_dragon
        "dark" -> R.drawable.type_dark
        "steel" -> R.drawable.type_steel
        "fairy" -> R.drawable.type_fairy
        else -> R.color.transparent
    }
}

fun parseStatNameAbbr(statName: String): String {
    return when (statName.lowercase()) {
        "hp" -> "HP"
        "attack" -> "Atk"
        "defense" -> "Def"
        "special-attack" -> "SpAtk"
        "special-defense" -> "SpDef"
        "speed" -> "Speed"
        else -> ""
    }
}

fun parseStatToColor(statName: String): Int {
    return when(statName.lowercase()) {
        "hp" -> R.color.hp_color
        "attack" -> R.color.atk_color
        "defense" -> R.color.def_color
        "special-attack" -> R.color.spatk_color
        "special-defense" -> R.color.spdef_color
        "speed" -> R.color.spd_color
        else -> R.color.black
    }
}