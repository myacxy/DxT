package net.myacxy.squinch.helpers.tracking.builder

import net.myacxy.squinch.helpers.tracking.TrackingHelper

class UserLogBuilder(trackingHelper: TrackingHelper) : PropertyLogBuilder<UserLogBuilder>(trackingHelper) {

    fun setEmail(email: String): UserLogBuilder {
        strings[PROPERTY_USER_EMAIL] = email
        return self()
    }

    fun setId(id: String): UserLogBuilder {
        strings[PROPERTY_USER_ID] = id
        return self()
    }

    fun setName(name: String): UserLogBuilder {
        strings[PROPERTY_USER_NAME] = name
        return self()
    }

    override fun post(): TrackingHelper {
        for (tracker in trackers) {
            tracker.user(booleans, ints, strings)
        }
        return trackingHelper
    }

    override fun self(): UserLogBuilder {
        return this
    }

    companion object {
        const val PROPERTY_USER_EMAIL = "user_email"
        const val PROPERTY_USER_ID = "user_id"
        const val PROPERTY_USER_NAME = "user_name"
    }
}
