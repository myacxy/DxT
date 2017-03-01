package net.myacxy.squinch.helpers.tracking;

import android.content.Context;

import net.myacxy.squinch.helpers.tracking.builder.ExceptionLogBuilder;
import net.myacxy.squinch.helpers.tracking.builder.LogBuilder;
import net.myacxy.squinch.helpers.tracking.builder.UserLogBuilder;

public class TrackingHelper {

    private static TrackingHelper INSTANCE;

    private TrackingHelper() {
    }

    public static void initialize(Context appContext) {
        INSTANCE = new TrackingHelper();
        for (Tracker tracker : Tracker.values()) {
            tracker.initialize(appContext);
        }
    }

    public static TrackingHelper getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("TrackingHelper not initialized");
        }
        return INSTANCE;
    }

    public ExceptionLogBuilder exception() {
        return new ExceptionLogBuilder(this);
    }

    public LogBuilder log() {
        return new LogBuilder(this);
    }

    public UserLogBuilder user() {
        return new UserLogBuilder(this);
    }

}
