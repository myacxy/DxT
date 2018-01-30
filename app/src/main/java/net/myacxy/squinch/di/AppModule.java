package net.myacxy.squinch.di;

import android.content.Context;

import net.myacxy.squinch.SquinchApplication;
import net.myacxy.squinch.helpers.tracking.ITracker;
import net.myacxy.squinch.helpers.tracking.Tracker;
import net.myacxy.squinch.helpers.tracking.TrackingHelper;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Provides
    @PerApplication
    @ApplicationContext
    public Context applicationContext(SquinchApplication application) {
        return application.getApplicationContext();
    }

    @Provides
    @PerApplication
    public TrackingHelper trackingHelper(@ApplicationContext Context context) {
        for (ITracker tracker : Tracker.values()) {
            tracker.initialize(context);
        }
        return new TrackingHelper();
    }
}
