package com.echo.pokepedia.util

sealed class NetworkResult<out R> {
    data class Success<out R>(val result: R): NetworkResult<R>()
    data class Failure(val exception: UiText? = null): NetworkResult<Nothing>()
}