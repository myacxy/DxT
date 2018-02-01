package net.myacxy.squinch.helpers.tracking

interface Tracker {

    fun exception(throwable: Throwable?)

    fun user(booleans: Map<String, Boolean>, ints: Map<String, Int>, strings: Map<String, String>)

    fun log(booleans: Map<String, Boolean>, ints: Map<String, Int>, strings: Map<String, String>)
}
