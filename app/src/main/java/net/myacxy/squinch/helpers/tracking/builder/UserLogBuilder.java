package net.myacxy.squinch.helpers.tracking.builder;

import net.myacxy.squinch.helpers.tracking.Tracker;
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
        return this;
    }

    public UserLogBuilder setId(String id) {
        properties.put(PROPERTY_USER_ID, id);
        return this;
    }

    public UserLogBuilder setName(String name) {
        properties.put(PROPERTY_USER_NAME, name);
        return this;
    }

    @Override
    public TrackingHelper post() {
        for (Tracker tracker : trackers) {
            tracker.user(properties);
        }
        return trackingHelper;
    }
}
