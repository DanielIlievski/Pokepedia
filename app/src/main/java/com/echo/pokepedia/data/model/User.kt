package com.echo.pokepedia.data.model

import com.google.firebase.Timestamp

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val date: Timestamp
)