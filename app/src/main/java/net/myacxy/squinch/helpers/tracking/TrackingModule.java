package net.myacxy.squinch.helpers.tracking;

import net.myacxy.squinch.di.PerApplication;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class TrackingModule {

    @Provides
    @PerApplication
    @Named("crash_tracker")
    public ITracker crashTracker() {
        return new CrashTracker();
    }

    @Provides
    @PerApplication
    @Named("log_tracker")
    public ITracker logTracker() {
        return new LogTracker();
    }

    @Provides
    @PerApplication
    @Named("debug_log_tracker")
    public ITracker debugLogTracker() {
        return new DebugLogTracker();
    }
}
