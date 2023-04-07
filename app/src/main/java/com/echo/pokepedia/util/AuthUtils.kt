package com.echo.pokepedia.util

import android.util.Patterns

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun doPasswordsMatch(password: String, repeatPassword: String): Boolean {
    return password == repeatPassword
}

fun isPasswordStrong(password: String): Boolean {
    val capitalRegex = Regex("[A-Z]")
    val numberRegex = Regex("\\d")
    val specialCharRegex = Regex("[^A-Za-z\\d]")

    return password.length >= 8 &&
            password.contains(capitalRegex) &&
            password.contains(numberRegex) &&
            password.contains(specialCharRegex)
}

fun isEmailFieldEmpty(email: String): Boolean {
    return email.isBlank() || email.isEmpty()
}

fun isPasswordFieldEmpty(password: String): Boolean {
    return password.isBlank() || password.isEmpty()
}

fun isFirstNameFieldEmpty(firstName: String): Boolean {
    return firstName.isBlank() || firstName.isEmpty()
}

fun isLastNameFieldEmpty(lastName: String): Boolean {
    return lastName.isBlank() || lastName.isEmpty()
}