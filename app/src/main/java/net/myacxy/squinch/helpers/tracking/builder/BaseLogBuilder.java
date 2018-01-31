package net.myacxy.squinch.helpers.tracking.builder;

import android.support.annotation.NonNull;

import net.myacxy.squinch.helpers.tracking.ITracker;
import net.myacxy.squinch.helpers.tracking.TrackingHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseLogBuilder<T extends BaseLogBuilder> {

    protected final TrackingHelper trackingHelper;

    protected List<ITracker> trackers = new ArrayList<>();

    public BaseLogBuilder(TrackingHelper trackingHelper) {
        this.trackingHelper = trackingHelper;
    }

    public T withTrackers(ITracker tracker, ITracker... trackers) {
        this.trackers.clear();
        addTracker(tracker);
        if (trackers != null) {
            for (ITracker tracker1 : trackers) {
                addTracker(tracker1);
            }
        }
        return self();
    }

    public T addTracker(ITracker tracker) {
        if (!trackers.contains(tracker)) {
            trackers.add(tracker);
        }
        return self();
    }

    @NonNull
    protected abstract T self();

    public abstract TrackingHelper post();
}
