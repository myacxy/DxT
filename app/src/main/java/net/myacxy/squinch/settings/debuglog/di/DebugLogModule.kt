package net.myacxy.squinch.settings.debuglog.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.myacxy.squinch.settings.debuglog.DebugLogFragment

@Module
internal abstract class DebugLogModule {

    @ContributesAndroidInjector
    internal abstract fun debugLogFragment(): DebugLogFragment
}
