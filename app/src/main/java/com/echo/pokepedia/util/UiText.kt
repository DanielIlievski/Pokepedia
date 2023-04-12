package com.echo.pokepedia.util

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    data class DynamicString(val text: String? = null) : UiText()
    class StringResource(@StringRes val resId: Int, vararg val args: Any) : UiText()

    fun asString(context: Context): String {
        return when(this) {
            is DynamicString -> text ?: ""
            is StringResource -> context.getString(resId, *args)
        }
    }
}