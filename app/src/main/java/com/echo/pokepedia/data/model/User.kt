package com.echo.pokepedia.data.model

import com.google.firebase.Timestamp

data class User(
    val fullName: String? = null,
    val email: String? = null,
    val date: Timestamp? = null,
    val id: String? = null
)