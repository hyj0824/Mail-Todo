package com.mailtodo.app.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mailtodo.app.data.model.AccountType
import com.mailtodo.app.data.model.ExtractedDate
import com.mailtodo.app.data.model.SyncStatus
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromAccountType(value: AccountType): String {
        return value.name
    }

    @TypeConverter
    fun toAccountType(value: String): AccountType {
        return AccountType.valueOf(value)
    }

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String {
        return value.name
    }

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus {
        return SyncStatus.valueOf(value)
    }

    @TypeConverter
    fun fromExtractedDatesList(value: List<ExtractedDate>?): String? {
        return value?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toExtractedDatesList(value: String?): List<ExtractedDate>? {
        return value?.let {
            val listType = object : TypeToken<List<ExtractedDate>>() {}.type
            Gson().fromJson(it, listType)
        }
    }
}
