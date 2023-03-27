package com.echo.pokepedia.data.model

import com.echo.pokepedia.R

data class IntroScreen(
    val title: String,
    val description: String,
    val buttonIcon: Int
)

fun getIntroScreens(): List<IntroScreen> = listOf(
    IntroScreen(
        "Welcome",
        "Welcome to Pokepedia, your ultimate Pokemon reference guide with a complete Pokedex",
        R.drawable.ic_next
    ),
    IntroScreen(
        "Filter",
        "Filter by type, generation, or ability to easily find the Pokemon you need and become a master of the Pokemon world",
        R.drawable.ic_next
    ),
    IntroScreen(
        "Create a team",
        "Create custom teams and add favorite Pokemon for easy access with Pokepedia",
        R.drawable.ic_done
    ),
)