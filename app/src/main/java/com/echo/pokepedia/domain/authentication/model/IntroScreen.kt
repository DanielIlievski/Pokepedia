package com.echo.pokepedia.domain.authentication.model

import com.echo.pokepedia.R

data class IntroScreen(
    val title: Int,
    val description: Int,
    val buttonIcon: Int
)

fun getIntroScreens(): List<IntroScreen> = listOf(
    IntroScreen(
        R.string.intro_screen_title_1,
        R.string.intro_screen_description_1,
        R.drawable.ic_next
    ),
    IntroScreen(
        R.string.intro_screen_title_2,
        R.string.intro_screen_description_2,
        R.drawable.ic_next
    ),
    IntroScreen(
        R.string.intro_screen_title_3,
        R.string.intro_screen_description_3,
        R.drawable.ic_done
    ),
)