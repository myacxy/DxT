package net.myacxy.squinch.helpers.tracking.builder

import net.myacxy.squinch.helpers.tracking.Tracker
import net.myacxy.squinch.helpers.tracking.TrackingHelper
import java.util.*

abstract class BaseLogBuilder<out T : BaseLogBuilder<T>>(protected val trackingHelper: TrackingHelper) {

    protected var trackers: MutableList<Tracker> = ArrayList()

    fun withTrackers(vararg trackers: Tracker): T {
        this.trackers = trackers.toMutableList()
        return self()
    }

    fun addTracker(tracker: Tracker): T {
        if (!trackers.contains(tracker)) {
            trackers.add(tracker)
        }
        return self()
    }

    protected abstract fun self(): T

    abstract fun post(): TrackingHelper
}
