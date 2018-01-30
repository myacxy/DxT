package net.myacxy.squinch.settings.di;

import android.app.Activity;

import net.myacxy.squinch.settings.SettingsActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

@Module(subcomponents = SettingsActivityComponent.class)
public abstract class SettingsActivityModule {

    @Binds
    @IntoMap
    @ActivityKey(SettingsActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> settingsActivityInjector(SettingsActivityComponent.Builder builder);
}
