package net.myacxy.squinch.helpers.tracking;

import net.myacxy.squinch.helpers.tracking.builder.ExceptionLogBuilder;
import net.myacxy.squinch.helpers.tracking.builder.LogBuilder;
import net.myacxy.squinch.helpers.tracking.builder.UserLogBuilder;

public class TrackingHelper {

    private final Tracker logcatTracker;
    private final Tracker deviceTracker;
    private final Tracker remoteTracker;

    public TrackingHelper(Tracker logcatTracker, Tracker deviceTracker, Tracker remoteTracker) {
        this.remoteTracker = remoteTracker;
        this.logcatTracker = logcatTracker;
        this.deviceTracker = deviceTracker;
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
