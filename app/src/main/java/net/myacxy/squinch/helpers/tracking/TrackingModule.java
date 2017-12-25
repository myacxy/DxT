package net.myacxy.squinch.helpers.tracking;

import net.myacxy.squinch.ApplicationScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class TrackingModule {

    @Provides
    @ApplicationScope
    @Named("crash_tracker")
    public ITracker crashTracker() {
        return new CrashTracker();
    }

    @Provides
    @ApplicationScope
    @Named("log_tracker")
    public ITracker logTracker() {
        return new LogTracker();
    }

    @Provides
    @ApplicationScope
    @Named("debug_log_tracker")
    public ITracker debugLogTracker() {
        return new DebugLogTracker();
    }
}
