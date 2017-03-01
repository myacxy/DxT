package net.myacxy.squinch.helpers.tracking.builder;

import net.myacxy.squinch.helpers.tracking.Tracker;
import net.myacxy.squinch.helpers.tracking.TrackingHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseLogBuilder<SELF extends BaseLogBuilder> {

    protected final TrackingHelper trackingHelper;

    protected List<Tracker> trackers = new ArrayList<>();

    public BaseLogBuilder(TrackingHelper trackingHelper) {
        this.trackingHelper = trackingHelper;
    }

    public SELF withTrackers(Tracker tracker, Tracker... trackers) {
        this.trackers.clear();
        addTracker(tracker);
        if (trackers != null) {
            for (Tracker tracker1 : trackers) {
                addTracker(tracker1);
            }
        }
        return (SELF) this;
    }

    public SELF addTracker(Tracker tracker) {
        if (!trackers.contains(tracker)) {
            trackers.add(tracker);
        }
        return (SELF) this;
    }

    public abstract TrackingHelper post();
}
