package net.myacxy.squinch.data.entity

import android.arch.persistence.room.ColumnInfo

data class DebugLogEntryEntity(
        @ColumnInfo(name = "time") val time: String,
        @ColumnInfo(name = "message") val message: String,
        @ColumnInfo(name = "type") val type: String
)
