package com.echo.pokepedia.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceProvider @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) {

    fun fetchString(strId: Int) = applicationContext.getString(strId)
}