package net.myacxy.squinch.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.myacxy.squinch.RetroTwitchJobService
import net.myacxy.squinch.TwitchExtension

@Module
internal abstract class ServicesModule {

    @ContributesAndroidInjector
    internal abstract fun twitchExtension(): TwitchExtension

    @ContributesAndroidInjector
    internal abstract fun retroTwitchJobService(): RetroTwitchJobService
}
