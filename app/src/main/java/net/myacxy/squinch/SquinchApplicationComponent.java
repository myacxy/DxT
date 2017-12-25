package net.myacxy.squinch;

import net.myacxy.squinch.views.activities.MainActivityModule;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Component(modules = {
        AndroidInjectionModule.class,
        AndroidSupportInjectionModule.class,
        SquichApplicationModule.class,
        NetworkModule.class,
        MainActivityModule.class
})
public interface SquinchApplicationComponent extends AndroidInjector<SquinchApplication> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<SquinchApplication> {


        abstract Builder appModule(SquichApplicationModule appModule);

        @Override
        public void seedInstance(SquinchApplication instance) {
            appModule(new SquichApplicationModule(instance));
        }
    }
}
