package net.myacxy.squinch.data.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "debug_log_entries")
data class DebugLogEntryEntity(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
        @ColumnInfo(name = "time") val time: String,
        @ColumnInfo(name = "message") val message: String,
        @ColumnInfo(name = "type") val type: String
)
