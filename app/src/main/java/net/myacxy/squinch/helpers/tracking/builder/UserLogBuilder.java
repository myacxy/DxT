package net.myacxy.squinch.helpers.tracking.builder;

import android.support.annotation.NonNull;

import net.myacxy.squinch.helpers.tracking.ITracker;
import net.myacxy.squinch.helpers.tracking.TrackingHelper;

public class UserLogBuilder extends PropertyLogBuilder<UserLogBuilder> {

    public static final String PROPERTY_USER_EMAIL = "user_email";
    public static final String PROPERTY_USER_ID = "user_id";
    public static final String PROPERTY_USER_NAME = "user_name";

    public UserLogBuilder(TrackingHelper trackingHelper) {
        super(trackingHelper);
    }

    public UserLogBuilder setEmail(String email) {
        properties.put(PROPERTY_USER_EMAIL, email);
        return self();
    }

    public UserLogBuilder setId(String id) {
        properties.put(PROPERTY_USER_ID, id);
        return self();
    }

    public UserLogBuilder setName(String name) {
        properties.put(PROPERTY_USER_NAME, name);
        return self();
    }

    @Override
    public TrackingHelper post() {
        for (ITracker tracker : trackers) {
            tracker.user(properties);
        }
        return trackingHelper;
    }

    @NonNull
    @Override
    protected UserLogBuilder self() {
        return this;
    }
}
