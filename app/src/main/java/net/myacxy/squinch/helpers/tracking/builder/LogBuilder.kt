package net.myacxy.squinch.helpers.tracking.builder

import net.myacxy.squinch.helpers.tracking.TrackingHelper

class LogBuilder(trackingHelper: TrackingHelper) : PropertyLogBuilder<LogBuilder>(trackingHelper) {

    override fun post(): TrackingHelper {
        trackers.forEach { it.log(booleans, ints, strings) }
        return trackingHelper
    }

    override fun self(): LogBuilder {
        return this
    }
}
