package net.myacxy.squinch.settings.channelselection.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.myacxy.squinch.settings.channelselection.ChannelSelectionFragment

@Module
internal abstract class ChannelSelectionModule {

    @ContributesAndroidInjector
    internal abstract fun channelSelectionFragment(): ChannelSelectionFragment
}
