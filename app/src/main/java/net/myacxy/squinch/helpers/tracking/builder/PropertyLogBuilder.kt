package net.myacxy.squinch.helpers.tracking.builder

import net.myacxy.squinch.helpers.tracking.TrackingHelper
import java.util.*

abstract class PropertyLogBuilder<out T : PropertyLogBuilder<T>>(trackingHelper: TrackingHelper) : BaseLogBuilder<T>(trackingHelper) {

    protected val booleans: MutableMap<String, Boolean> = HashMap()
    protected val ints: MutableMap<String, Int> = HashMap()
    protected val strings: MutableMap<String, String> = HashMap()

    fun addProperty(key: String, value: Boolean): T {
        booleans[key] = value
        return self()
    }

    fun addProperty(key: String, value: Int): T {
        ints[key] = value
        return self()
    }

    fun addProperty(key: String, value: String): T {
        strings[key] = value
        return self()
    }
}
