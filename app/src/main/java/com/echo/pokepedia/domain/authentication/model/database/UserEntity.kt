package com.echo.pokepedia.domain.authentication.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity("user")
data class UserEntity(
    @ColumnInfo("full_name") val fullName: String,
    @ColumnInfo("email") val email: String,
    @ColumnInfo("date") val date: Date,
    @ColumnInfo("profile_picture") val profilePicture: String? = null,
    @PrimaryKey(autoGenerate = false) @ColumnInfo("firebase_id") val firebaseId: String,
)