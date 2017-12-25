package net.myacxy.squinch.views.activities;


import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent(modules = {})
public interface MainActivitySubcomponent extends AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<MainActivity> {
    }
}
