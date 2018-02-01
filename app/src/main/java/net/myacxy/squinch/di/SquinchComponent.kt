package net.myacxy.squinch.di

import com.crashlytics.android.Crashlytics
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import net.myacxy.squinch.SquinchApplication
import net.myacxy.squinch.helpers.tracking.TrackingModule
import net.myacxy.squinch.settings.di.MainSettingsModule

@PerApplication
@Component(modules = [
    AndroidSupportInjectionModule::class,
    SquinchModule::class,
    ServicesModule::class,
    TrackingModule::class,
    NetworkModule::class,
    MainSettingsModule::class
])
internal interface SquinchComponent : AndroidInjector<SquinchApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<SquinchApplication>() {

        @BindsInstance
        abstract fun crashlytics(crashlytics: Crashlytics): Builder
    }
}
