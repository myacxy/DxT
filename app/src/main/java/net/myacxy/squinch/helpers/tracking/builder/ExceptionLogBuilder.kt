package net.myacxy.squinch.helpers.tracking.builder

import net.myacxy.squinch.helpers.tracking.TrackingHelper

class ExceptionLogBuilder(trackingHelper: TrackingHelper) : BaseLogBuilder<ExceptionLogBuilder>(trackingHelper) {

    private var throwable: Throwable? = null

    fun withThrowable(throwable: Throwable?): ExceptionLogBuilder {
        this.throwable = throwable
        return self()
    }

    override fun post(): TrackingHelper {
        trackers.forEach { it.exception(throwable) }
        return trackingHelper
    }

    override fun self(): ExceptionLogBuilder {
        return this
    }
}
