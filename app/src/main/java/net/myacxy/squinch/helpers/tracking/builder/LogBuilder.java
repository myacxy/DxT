package net.myacxy.squinch.helpers.tracking.builder;

import net.myacxy.squinch.helpers.tracking.Tracker;
import net.myacxy.squinch.helpers.tracking.TrackingHelper;

public class LogBuilder extends PropertyLogBuilder<LogBuilder> {

    public LogBuilder(TrackingHelper trackingHelper) {
        super(trackingHelper);
    }

    @Override
    public TrackingHelper post() {
        for (Tracker tracker : trackers) {
            tracker.log(properties);
        }
        return trackingHelper;
    }
}
