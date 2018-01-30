package net.myacxy.squinch.settings.di;

import net.myacxy.squinch.di.PerActivity;
import net.myacxy.squinch.settings.SettingsActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent
@PerActivity
public interface SettingsActivityComponent extends AndroidInjector<SettingsActivity> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<SettingsActivity> {
    }
}
