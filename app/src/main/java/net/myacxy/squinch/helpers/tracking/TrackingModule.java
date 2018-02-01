package net.myacxy.squinch.helpers.tracking;

import android.content.Context;
import android.content.SharedPreferences;

import com.crashlytics.android.Crashlytics;

import net.myacxy.squinch.base.di.ApplicationContext;
import net.myacxy.squinch.base.di.PerApplication;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber.Tree;

@Module
public class TrackingModule {

    @Provides
    @PerApplication
    @Named("debug_log")
    public SharedPreferences debugLogSharedPreferences(@ApplicationContext Context context) {
        return context.getSharedPreferences("debug_log", Context.MODE_PRIVATE);
    }

    @Provides
    @PerApplication
    @Named("debug")
    public Tree debugTree() {
        return new TimberTracker.DebugTree();
    }

    @Provides
    @PerApplication
    @Named("logcat")
    public Tracker logcatTracker(@Named("debug") Tree debugTree) {
        return new TimberTracker(debugTree);
    }

    @Provides
    @PerApplication
    @Named("device")
    public Tracker deviceTracker(@Named("debug_log") SharedPreferences sharedPreferences) {
        return new SharedPreferencesTracker(sharedPreferences);
    }

    @Provides
    @PerApplication
    @Named("remote")
    public Tracker remoteTracker(Crashlytics crashlytics) {
        return new CrashlyticsTracker(crashlytics);
    }

    @Provides
    @PerApplication
    public TrackingHelper trackingHelper(
            @Named("logcat") Tracker logcatTracker,
            @Named("device") Tracker deviceTracker,
            @Named("remote") Tracker remoteTracker
    ) {
        return new TrackingHelper(logcatTracker, deviceTracker, remoteTracker);
    }
}
