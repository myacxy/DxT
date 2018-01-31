package net.myacxy.squinch.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import net.myacxy.squinch.SquinchApplication
import net.myacxy.squinch.settings.di.MainSettingsModule

@PerApplication
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    NetworkModule::class,
    MainSettingsModule::class
])
internal interface SquinchComponent : AndroidInjector<SquinchApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<SquinchApplication>()
}

