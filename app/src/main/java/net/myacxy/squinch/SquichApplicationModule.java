package net.myacxy.squinch;

import android.content.Context;

import net.myacxy.squinch.helpers.tracking.ITracker;
import net.myacxy.squinch.helpers.tracking.Tracker;
import net.myacxy.squinch.helpers.tracking.TrackingHelper;

import dagger.Module;
import dagger.Provides;

@Module
public class SquichApplicationModule {

    private final SquinchApplication application;

    public SquichApplicationModule(SquinchApplication application) {
        this.application = application;
    }

    @Provides
    @ApplicationScope
    @ApplicationContext
    public Context appContext() {
        return application.getApplicationContext();
    }

    @Provides
    @ApplicationScope
    public TrackingHelper trackingHelper(@ApplicationContext Context context) {
        for (ITracker tracker : Tracker.values()) {
            tracker.initialize(context);
        }
        return new TrackingHelper();
    }
}
