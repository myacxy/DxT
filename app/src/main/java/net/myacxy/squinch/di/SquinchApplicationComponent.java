package net.myacxy.squinch.di;

import net.myacxy.squinch.SquinchApplication;
import net.myacxy.squinch.settings.di.SettingsActivityModule;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@PerApplication
@Component(modules = {
        AndroidSupportInjectionModule.class,
        AppModule.class,
        NetworkModule.class,
        SettingsActivityModule.class
})
interface SquinchApplicationComponent extends AndroidInjector<SquinchApplication> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<SquinchApplication> {
    }
}

