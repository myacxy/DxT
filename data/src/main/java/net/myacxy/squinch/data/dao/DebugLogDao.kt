package net.myacxy.squinch.data.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.support.annotation.CheckResult
import io.reactivex.Flowable
import net.myacxy.squinch.data.entity.DebugLogEntryEntity

@Dao
abstract class DebugLogDao {

    @CheckResult
    @Query("SELECT * FROM debug_log_entries")
    abstract fun getAllDebugLogEntries(): Flowable<List<DebugLogEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(debugLogEntries: List<DebugLogEntryEntity>)

    @Query("DELETE FROM debug_log_entries")
    abstract fun deleteAll()
}
