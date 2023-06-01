package com.echo.pokepedia.domain.authentication.model.database.typeconverter

import androidx.room.TypeConverter
import java.util.*

class DateTypeConverter {

    @TypeConverter
    fun fromLong(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToLong(date: Date?): Long? {
        return date?.time
    }
}