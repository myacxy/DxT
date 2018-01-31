package net.myacxy.squinch.settings.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.myacxy.squinch.settings.SettingsActivity
import net.myacxy.squinch.settings.channelselection.di.ChannelSelectionModule
import net.myacxy.squinch.settings.debuglog.di.DebugLogModule

@Module
internal abstract class MainSettingsModule {

    @ContributesAndroidInjector(modules = [
        SettingsModule::class,
        ChannelSelectionModule::class,
        DebugLogModule::class
    ])
    internal abstract fun settingsActivity(): SettingsActivity
}
