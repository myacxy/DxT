package net.myacxy.squinch.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import net.myacxy.squinch.data.dao.DebugLogDao
import net.myacxy.squinch.data.entity.DebugLogEntryEntity

@Database(entities = [
    DebugLogEntryEntity::class
], version = 1)
abstract class SquinchDatabase : RoomDatabase() {

    abstract fun debugLogDao(): DebugLogDao
}
