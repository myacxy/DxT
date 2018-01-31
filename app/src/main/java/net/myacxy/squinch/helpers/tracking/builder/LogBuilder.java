package net.myacxy.squinch.helpers.tracking.builder;

import android.support.annotation.NonNull;

import net.myacxy.squinch.helpers.tracking.ITracker;
import net.myacxy.squinch.helpers.tracking.TrackingHelper;

public class LogBuilder extends PropertyLogBuilder<LogBuilder> {

    public LogBuilder(TrackingHelper trackingHelper) {
        super(trackingHelper);
    }

    @Override
    public TrackingHelper post() {
        for (ITracker tracker : trackers) {
            tracker.log(properties);
        }
        return trackingHelper;
    }

    @NonNull
    @Override
    protected LogBuilder self() {
        return this;
    }
}
