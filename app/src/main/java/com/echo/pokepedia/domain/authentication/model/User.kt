package com.echo.pokepedia.domain.authentication.model

import java.util.*

data class User(
    val fullName: String? = null,
    val email: String? = null,
    val date: Date? = null,
    val profilePicture: String? = null,
    val firebaseId: String? = null
)