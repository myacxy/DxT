package net.myacxy.squinch.settings.debuglog

import android.support.annotation.StringDef

class DebugLogEntry(val time: Long, @DebugLogEntryType val type: String, val message: String) : Comparable<DebugLogEntry> {

    override fun compareTo(other: DebugLogEntry): Int {
        return time.compareTo(other.time)
    }

    @StringDef(TYPE_LOG, TYPE_EXCEPTION, TYPE_USER)
    annotation class DebugLogEntryType

    companion object {

        const val TYPE_LOG = "Log"
        const val TYPE_EXCEPTION = "Exception"
        const val TYPE_USER = "User"
    }
}
