package net.myacxy.squinch.helpers.tracking.builder;

import net.myacxy.squinch.helpers.tracking.Tracker;
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
        for (Tracker tracker : trackers) {
            tracker.exception(throwable);
        }
        return trackingHelper;
    }
}
