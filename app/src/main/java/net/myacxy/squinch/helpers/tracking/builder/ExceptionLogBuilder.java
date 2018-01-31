package net.myacxy.squinch.helpers.tracking.builder;

import android.support.annotation.NonNull;

import net.myacxy.squinch.helpers.tracking.ITracker;
import net.myacxy.squinch.helpers.tracking.TrackingHelper;

public class ExceptionLogBuilder extends BaseLogBuilder<ExceptionLogBuilder> {

    private Throwable throwable;

    public ExceptionLogBuilder(TrackingHelper trackingHelper) {
        super(trackingHelper);
    }

    public ExceptionLogBuilder withThrowable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    @Override
    public TrackingHelper post() {
        for (ITracker tracker : trackers) {
            tracker.exception(throwable);
        }
        return trackingHelper;
    }

    @NonNull
    @Override
    protected ExceptionLogBuilder self() {
        return this;
    }
}
