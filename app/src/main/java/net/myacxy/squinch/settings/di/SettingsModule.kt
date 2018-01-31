package net.myacxy.squinch.settings.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.myacxy.squinch.settings.SettingsFragment

@Module
internal abstract class SettingsModule {

    @ContributesAndroidInjector
    internal abstract fun settingsFragment(): SettingsFragment
}
