package com.echo.pokepedia.data.mappers

import com.echo.pokepedia.domain.authentication.model.User
import com.echo.pokepedia.domain.authentication.model.database.UserEntity
import com.google.firebase.auth.FirebaseUser
import java.util.*

fun User.toUserEntity(): UserEntity {
    return UserEntity(
        fullName = this.fullName ?: "",
        email = this.email ?: "",
        date = this.date ?: Date(),
        profilePicture = this.profilePicture,
        firebaseId = this.firebaseId ?: ""
    )
}

fun UserEntity?.toUser(): User {
    return User(
        fullName = this?.fullName,
        email = this?.email,
        date = this?.date,
        profilePicture = this?.profilePicture,
        firebaseId = this?.firebaseId
    )
}

fun FirebaseUser.toUser(): User {
    return User(
        fullName = this.displayName,
        date = Date(),
        email = this.email,
        firebaseId = this.uid,
        profilePicture = this.photoUrl.toString()
    )
}