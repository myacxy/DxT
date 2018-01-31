package net.myacxy.squinch.helpers.tracking.builder;

import net.myacxy.squinch.helpers.tracking.TrackingHelper;

import java.util.HashMap;
import java.util.Map;

public abstract class PropertyLogBuilder<T extends PropertyLogBuilder> extends BaseLogBuilder<T> {

    protected Map<String, String> properties = new HashMap<>();

    public PropertyLogBuilder(TrackingHelper trackingHelper) {
        super(trackingHelper);
    }

    public T addProperty(String key, String value) {
        properties.put(key, value);
        return self();
    }
}
